package finalyearproject.nearu.helper;

/**
 * Created by deepakgavkar on 24/04/16.
 */

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CustomStringRequest extends Request<String> {

    private Listener<String> listener;
    private Map<String, String> params;

    public CustomStringRequest(String url, Map<String, String> params,
                               Listener<String> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public CustomStringRequest(int method, String url, Map<String, String> params,
                               Listener<String> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

//    public CustomRequest(int method, String url, Map<String, String> params,
//                         Listener<String> reponseListener, ErrorListener errorListener) {
//        super(method, url, errorListener);
//        this.listener1 = reponseListener;
//        this.params = params;
//    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(jsonString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception jee) {
            return Response.error(new VolleyError(jee));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}

