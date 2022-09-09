
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

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NoConnectionsException;
import org.voltdb.client.ProcCallException;

public class TokenDemo {

    Client mainClient;

    public TokenDemo(Client mainClient) {
        super();
        this.mainClient = mainClient;
    }

    /**
     * Print a formatted message.
     *
     * @param message
     */
    public static void msg(String message) {

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        System.out.println(strDate + ":" + message);

    }

    /**
     * Connect to VoltDB using a comma delimited hostname list.
     *
     * @param commaDelimitedHostnames
     * @return
     * @throws Exception
     */
    protected static Client connectVoltDB(String commaDelimitedHostnames) throws Exception {
        Client client = null;
        ClientConfig config = null;

        try {
            msg("Logging into VoltDB");

            config = new ClientConfig(); // "admin", "idontknow");
            config.setTopologyChangeAware(true);
            config.setReconnectOnConnectionLoss(true);

            client = ClientFactory.createClient(config);

            String[] hostnameArray = commaDelimitedHostnames.split(",");

            for (String element : hostnameArray) {
                msg("Connect to " + element + "...");
                try {
                    client.createConnection(element);
                } catch (Exception e) {
                    msg(e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("VoltDB connection failed.." + e.getMessage(), e);
        }

        return client;

    }

    protected void upsertAllUsers(int userCount, int tpMs)
            throws InterruptedException, IOException, NoConnectionsException {

        final long startMsUpsert = System.currentTimeMillis();
        long currentMs = System.currentTimeMillis();
        int tpThisMs = 0;

        for (int i = 0; i < userCount; i++) {

            if (tpThisMs++ > tpMs) {

                while (currentMs == System.currentTimeMillis()) {
                    Thread.sleep(0, 50000);
                }

                currentMs = System.currentTimeMillis();
                tpThisMs = 0;
            }

            ComplainOnErrorCallback coec = new ComplainOnErrorCallback();

            mainClient.callProcedure(coec, "token_users.INSERT", i, 0);

            if (i % 100000 == 1) {
                msg("Upserted " + i + " users...");

            }

        }

        msg("All " + userCount + " entries in queue, waiting for it to drain...");
        mainClient.drain();

        long entriesPerMS = userCount / (System.currentTimeMillis() - startMsUpsert);
        msg("Upserted " + entriesPerMS + " users per ms...");
    }

    public static void main(String[] args) {

        msg("Parameters:" + Arrays.toString(args));

        if (args.length < 3 || args.length > 4) {
            msg("Usage: hostnames CREATE_USERS how_many ");
            msg("or ");
            msg("Usage: hostnames GET_TOKEN userid ");
            msg("or ");
            msg("Usage: hostnames USE_TOKEN userid token ");
            System.exit(1);
        }
        try {
            // Comma delimited list of hosts...
            String hostlist = args[0];

            Client mainClient = connectVoltDB(hostlist);

            TokenDemo td = new TokenDemo(mainClient);

            if (args[1].equalsIgnoreCase("CREATE_USERS")) {
                td.upsertAllUsers(Integer.parseInt(args[2]), 50);
            } else if (args[1].equalsIgnoreCase("GET_TOKEN")) {
                td.getToken(Integer.parseInt(args[2]));
            } else if (args[1].equalsIgnoreCase("USE_TOKEN")) {
                td.useToken(Integer.parseInt(args[2]), args[3]);
            }

            msg("Closing connection...");
            mainClient.close();

        } catch (Exception e) {
            msg(e.getMessage());
        }

    }

    private void getToken(int userId) {

        String txnId = "TXN" + System.currentTimeMillis();

        try {
            ClientResponse cr = mainClient.callProcedure("CreateToken", userId,
                    new Date(System.currentTimeMillis() + 60 * 10 * 1000), txnId, 5);

            msg(cr.getAppStatusString());

            for (int i = 0; i < cr.getResults().length; i++) {
                msg(cr.getResults()[i].toFormattedString());
            }

        } catch (IOException | ProcCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void useToken(int userId, String tokenId) {

        String txnId = "TXN" + System.currentTimeMillis();

        try {
            ClientResponse cr = mainClient.callProcedure("UseToken", userId, tokenId, txnId);

            msg(cr.getAppStatusString());

            for (int i = 0; i < cr.getResults().length; i++) {
                msg(cr.getResults()[i].toFormattedString());
            }

        } catch (IOException | ProcCallException e) {
            e.printStackTrace();
        }

    }

}
