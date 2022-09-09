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

public class CreateToken extends VoltProcedure {

    // @formatter:off


    public static final SQLStmt getUser = new SQLStmt("SELECT userid FROM token_users "
            + "WHERE userid = ?;");

    public static final SQLStmt getTxn = new SQLStmt("SELECT txn_time FROM user_recent_transactions "
            + "WHERE userid = ? AND user_txn_id = ?;");

	public static final SQLStmt addTxn = new SQLStmt("INSERT INTO user_recent_transactions "
			+ "(userid, user_txn_id, txn_time, purpose) VALUES (?,?,NOW,?);");

    public static final SQLStmt reportError = new SQLStmt(
            "INSERT INTO user_error_events (userid,user_txn_id,status_code,message) VALUES (?,?,?,?);");

    public static final SQLStmt addToken = new SQLStmt(
            "INSERT INTO secure_token_table (userid,token_id, remaining_usages,expiry_date,create_date) VALUES (?,?,?,?,NOW);");

    public static final SQLStmt incTokenCount = new SQLStmt(
            "UPDATE token_users SET token_count = token_count + 1 WHERE userid = ?;");

    public static final SQLStmt getToken = new SQLStmt(
            "SELECT * FROM secure_token_table WHERE userid = ? AND token_id = ?;");



	// @formatter:on

    public VoltTable[] run(long userId, TimestampType expiryDate, String txnId, int usageCount)
            throws VoltAbortException {

        voltQueueSQL(getTxn, userId, txnId);
        voltQueueSQL(getUser, userId);

        VoltTable[] results = voltExecuteSQL();

        if (results[0].advanceRow()) {

            reportError(userId, txnId, UseToken.STATUS_TXN_ALREADY_HAPPENED,
                    "Event already happened at " + results[0].getTimestampAsTimestamp("txn_time").toString());

            return voltExecuteSQL(true);

        } else {
            voltQueueSQL(addTxn, userId, txnId, "Use Token");
        }

        if (isInvalidTransactionId(userId, txnId)) {

            reportError(userId, txnId, UseToken.STATUS_BAD_TRANSACTION_FORMAT, "Transaction ID is in the wrong format");
            return voltExecuteSQL(true);

        }

        if (!results[1].advanceRow()) {

            reportError(userId, txnId, UseToken.STATUS_USER_DOES_NOT_EXIST, "No such user");
            return voltExecuteSQL(true);

        }

        final String tokenId = getSecureToken(userId, txnId);

        voltQueueSQL(addToken, userId, tokenId, usageCount, expiryDate);
        voltQueueSQL(incTokenCount, userId);

        voltQueueSQL(getToken, userId, tokenId);

        this.setAppStatusCode(UseToken.STATUS_OK);
        this.setAppStatusString("Created token");

        return voltExecuteSQL(true);
    }

    /**
     * Get a unique, non-guesssable token for userId.
     * @param userId
     * @param txnId
     * @return
     */
    private String getSecureToken(long userId, String txnId) {

        StringBuffer b = new StringBuffer();
        b.append(userId);
        b.append('-');
        b.append(this.getUniqueId());
        b.append('-');
        b.append(this.getSeededRandomNumberGenerator().nextLong());

        return encrypt(b.toString());
    }

     private String encrypt(String data) {

        StringBuffer b = new StringBuffer(data);

        // How you *actually* encrypt this is up to you - any deterministic Java
        // mechanism will wok...
        return b.reverse().toString();
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
