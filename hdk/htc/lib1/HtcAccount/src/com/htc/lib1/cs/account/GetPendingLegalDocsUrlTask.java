
package com.htc.lib1.cs.account;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.content.Context;
import android.os.Handler;

import com.htc.lib1.cs.CallbackFutureTask;

/**
 * {@link FutureTask} encapsulation of {@link GetPendingLegalDocsUrlCallable}.
 * 
 * @author samael_wang
 */
public class GetPendingLegalDocsUrlTask extends CallbackFutureTask<String> {

    /**
     * @param context Context to operate on.
     */
    public GetPendingLegalDocsUrlTask(Context context) {
        super(new GetPendingLegalDocsUrlCallable(context));
    }

    /**
     * @param context Context to operate on.
     * @param callback Callback to invoke when the task is done.
     * @param handler Handler to decide which thread the {@code callback} should
     *            be executed on.
     */
    public GetPendingLegalDocsUrlTask(Context context,
            com.htc.lib1.cs.CallbackFutureTask.Callback<String> callback, Handler handler) {
        super(new GetPendingLegalDocsUrlCallable(context), callback, handler);
    }

    /**
     * Callable to get available countries of HTC Account service and compose
     * corresponding terms and conditions URL.
     * 
     * @author samael_wang
     */
    public static class GetPendingLegalDocsUrlCallable implements Callable<String> {
        private Context mmContext;

        /**
         * @param context Context to operate on.
         */
        public GetPendingLegalDocsUrlCallable(Context context) {
            if (context == null)
                throw new IllegalArgumentException("'context' is null.");

            mmContext = context;
        }

        /**
         * Get the web URL of HTC Account terms and conditions which can be
         * shown in a browser or webview.
         * 
         * @return Web URL which can be shown in a browser or webview.
         * @throws GetRegionsFailedException If not able to find available
         *             countries of HTC Account service.
         */
        @Override
        public String call() throws GetRegionsFailedException {
            RegionsHelper regionsHelper = RegionsHelper.get(mmContext);
            regionsHelper.blockingGetRegions();
            String countryCode = regionsHelper.getSuggestedCountryCode();
            return new ConfigurationResource(mmContext)
                    .getPendingLegalDocsUrl(countryCode, null /* locale */);
        }

    }
}
