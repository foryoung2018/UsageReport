
package com.htc.lib1.cs.account.restservice;

import android.content.Context;

import com.htc.lib1.cs.httpclient.HtcRestRequestPropertiesBuilder;

/**
 * Identity customized request properties builder which simply adds source
 * service name.
 * 
 * @author samael_wang@htc.com
 */
public class IdentityRequestPropertiesBuilder extends HtcRestRequestPropertiesBuilder {

    public IdentityRequestPropertiesBuilder(Context context, String sourceService) {
        super(context);
        addRequestProperty("X-HTC-ORIGIN", sourceService);
    }

}
