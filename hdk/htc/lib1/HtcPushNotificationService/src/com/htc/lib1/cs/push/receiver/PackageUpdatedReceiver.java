
package com.htc.lib1.cs.push.receiver;

/**
 * Receiver to update registration after package update.
 * 
 * @author samael_wang@htc.com
 */
public class PackageUpdatedReceiver extends AbstractIntentServiceBroadcastReceiver {

    public static class HandleBroadcastServiceImpl extends HandleBroadcastService {
        private BroadcastHandler mmHandler = new PackageUpdatedHandler();

        public HandleBroadcastServiceImpl() {
            super(PackageUpdatedReceiver.class.getSimpleName());
        }

        @Override
        protected BroadcastHandler[] getHandlers() {
            return new BroadcastHandler[] {
                    mmHandler
            };
        }
    }

    @Override
    protected Class<? extends HandleBroadcastService> getServiceClass() {
        return HandleBroadcastServiceImpl.class;
    }
}
