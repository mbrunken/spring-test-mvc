package org.springframework.test.web.client;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.RequestMatcherClientHttpRequest;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mbrunken
 * Date: 10/24/12
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class SmartClientHttpRequest extends MockClientHttpRequest {

    protected MockRestServiceServer mockRestServiceServer;

    public SmartClientHttpRequest(MockRestServiceServer mockRestServiceServer) {
        this.mockRestServiceServer = mockRestServiceServer;
    }


    public ClientHttpResponse execute() throws IOException {

        RequestMatcherClientHttpRequest matchedRequest = null;

        for(MockClientHttpRequest request:mockRestServiceServer.getExpectedRequests()){

            RequestMatcherClientHttpRequest matcherClientHttpRequest = (RequestMatcherClientHttpRequest) request;

            try {
                matcherClientHttpRequest.matches(this);
                matchedRequest = matcherClientHttpRequest;
            } catch (AssertionError e) {
                continue;
            }

        }

        if(matchedRequest == null){
            throw new AssertionError("No request found");
        }

        setResponse(matchedRequest.getResponseCreator().createResponse(this));

        return super.execute();
    }

}
