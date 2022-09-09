
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;

public class ComplainOnErrorCallback implements ProcedureCallback {

    @Override
    public void clientCallback(ClientResponse arg0) throws Exception {

        if (arg0.getStatus() != ClientResponse.SUCCESS) {
            TokenDemo.msg("Error Code " + arg0.getStatusString());
        }

    }

}
