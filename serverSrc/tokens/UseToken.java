package tokens;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2022 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.types.TimestampType;

public class UseToken extends VoltProcedure {

    // @formatter:off


    public static final SQLStmt getUser = new SQLStmt("SELECT userid, token_count FROM token_users "
            + "WHERE userid = ?;");

    public static final SQLStmt getTxn = new SQLStmt("SELECT txn_time FROM user_recent_transactions "
            + "WHERE userid = ? AND user_txn_id = ?;");

    public static final SQLStmt getToken = new SQLStmt("SELECT * FROM secure_token_table WHERE userid = ? AND token_id = ?;");

    public static final SQLStmt getErrorCount = new SQLStmt("SELECT how_many FROM user_error_events_summary WHERE userid = ?;");

    public static final SQLStmt decrementToken = new SQLStmt("UPDATE  secure_token_table SET remaining_usages = remaining_usages -1 "
            + "WHERE userid = ? AND token_id = ? AND remaining_usages >= 1 AND expiry_date >= NOW;");

	public static final SQLStmt addTxn = new SQLStmt("INSERT INTO user_recent_transactions "
			+ "(userid, user_txn_id, txn_time, purpose) VALUES (?,?,NOW,?);");

    public static final SQLStmt reportError = new SQLStmt(
            "INSERT INTO user_error_events (userid,user_txn_id,status_code,message) VALUES (?,?,?,?);");


    public static final byte STATUS_OK = 0;
    public static final byte STATUS_TXN_ALREADY_HAPPENED = -1;
    public static final byte STATUS_USER_DOES_NOT_EXIST = -2;
    public static final byte STATUS_BAD_TRANSACTION_FORMAT = -3;
    public static final byte STATUS_TOKEN_DOES_NOT_EXIST = -4;
    public static final byte STATUS_TOKEN_USED =-5;
    public static final byte STATUS_TOKEN_EXPIRED = -5;

    public static final byte STATUS_USER_HAS_TOO_MANY_ERRORS = 6;

	// @formatter:on

    public VoltTable[] run(long userId, String tokenId, String txnId) throws VoltAbortException {

        long tokenCountForThisUser = 0;

        voltQueueSQL(getTxn, userId, txnId);
        voltQueueSQL(getUser, userId);
        voltQueueSQL(getToken, userId, tokenId);
        voltQueueSQL(getErrorCount, userId);

        VoltTable[] results = voltExecuteSQL();

        if (results[0].advanceRow()) {

            reportError(userId, txnId, STATUS_TXN_ALREADY_HAPPENED,
                    "Event already happened at " + results[0].getTimestampAsTimestamp("txn_time").toString());

            return voltExecuteSQL(true);

        } else {
            voltQueueSQL(addTxn, userId, txnId, "Use Token");
        }

        if (isInvalidTransactionId(userId, txnId)) {

            reportError(userId, txnId, STATUS_BAD_TRANSACTION_FORMAT, "Transaction ID is in the wrong format");
            return voltExecuteSQL(true);

        }

        if (!results[1].advanceRow()) {

            reportError(userId, txnId, STATUS_USER_DOES_NOT_EXIST, "No such user");
            return voltExecuteSQL(true);

        } else {
            tokenCountForThisUser = results[1].getLong("token_count");
        }

        if (!results[2].advanceRow()) {

            reportError(userId, txnId, STATUS_TOKEN_DOES_NOT_EXIST, "Token " + tokenId + " does not exist");

            return voltExecuteSQL(true);

        }

        final long remainingUsages = results[2].getLong("remaining_usages");
        final TimestampType expiryDate = results[2].getTimestampAsTimestamp("expiry_date");

        if (results[3].advanceRow()) {

            long errorCount = results[3].getLong("how_many");

            if (errorCount > 2 && errorCount > tokenCountForThisUser) {
                reportError(userId, txnId, STATUS_USER_HAS_TOO_MANY_ERRORS, "User " + userId + " has " + errorCount
                        + " errors and only " + tokenCountForThisUser + " successes");

                return voltExecuteSQL(true);
            }
        }

        if (remainingUsages < 1) {
            reportError(userId, txnId, STATUS_TOKEN_USED, "Token " + tokenId + " used");

            return voltExecuteSQL(true);

        }

        if (expiryDate.asApproximateJavaDate().before(this.getTransactionTime())) {
            reportError(userId, txnId, STATUS_TOKEN_EXPIRED,
                    "Token " + tokenId + " expired at " + expiryDate.toString());

            return voltExecuteSQL(true);

        }

        voltQueueSQL(decrementToken, EXPECT_ONE_ROW, userId, tokenId);
        voltQueueSQL(getToken, userId, tokenId);

        this.setAppStatusCode(STATUS_OK);
        this.setAppStatusString("subtracted one from token");

        return voltExecuteSQL(true);
    }

    private boolean isInvalidTransactionId(long userId, String txnId) {

        if (txnId.startsWith(userId + "-")) {
            return true;
        }
        return false;
    }

    private void reportError(long userId, String txnId, byte status, String errorMessage) {
        voltQueueSQL(reportError, userId, txnId, status, errorMessage);
        this.setAppStatusCode(status);
        this.setAppStatusString(errorMessage);

    }
}
