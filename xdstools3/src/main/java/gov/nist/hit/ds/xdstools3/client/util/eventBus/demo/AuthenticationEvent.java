package gov.nist.hit.ds.xdstools3.client.util.eventBus.demo;

import com.google.gwt.event.shared.GwtEvent;


    public class AuthenticationEvent extends GwtEvent<AuthenticationEventHandler> {

        public static Type<AuthenticationEventHandler> TYPE = new Type<AuthenticationEventHandler>();

        @Override
        public Type<AuthenticationEventHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(AuthenticationEventHandler handler) {
            handler.onAuthenticationChanged(this);
        }
}