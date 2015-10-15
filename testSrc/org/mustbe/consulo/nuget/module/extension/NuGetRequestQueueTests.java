package org.mustbe.consulo.nuget.module.extension;

import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author warty
 * @since 10/15/15
 */
public class NuGetRequestQueueTests extends Assert
{
    private final NuGetRequestQueue testObj = new NuGetRequestQueue();

    @Test
    public void testNuGetOrg()
    {
        testRequest("http://nuget.org/api/v2/FindPackagesById()?id=%27NLog%27&includePrerelease=true");
    }

    @Test
    public void testDargonIo()
    {
        testRequest("https://nuget.dargon.io/FindPackagesById()?id=%27NLog%27&includePrerelease=true");
    }

    private void testRequest(String url) {
        final String kExpectedResult = "expected_result";
        String result = testObj.request(url, new HttpRequests.RequestProcessor<String>() {
            @Override
            public String process(@NotNull HttpRequests.Request request) throws IOException {
                System.out.println("Request Successful:" + request.isSuccessful());
                return kExpectedResult;
            }
        });
        System.out.println("Expected result: '" + kExpectedResult + "' and got: '" + result + "' for url " + url);
        assertEquals(kExpectedResult, result);
    }
}
