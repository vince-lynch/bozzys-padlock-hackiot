package android.support.v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.os.BuildCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public final class MediaBrowserCompat {
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
    public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
    static final String TAG = "MediaBrowserCompat";
    private final MediaBrowserImpl mImpl;

    private static class CallbackHandler extends Handler {
        private final WeakReference<MediaBrowserServiceCallbackImpl> mCallbackImplRef;
        private WeakReference<Messenger> mCallbacksMessengerRef;

        CallbackHandler(MediaBrowserServiceCallbackImpl mediaBrowserServiceCallbackImpl) {
            this.mCallbackImplRef = new WeakReference<>(mediaBrowserServiceCallbackImpl);
        }

        public void handleMessage(Message message) {
            if (this.mCallbacksMessengerRef != null && this.mCallbacksMessengerRef.get() != null && this.mCallbackImplRef.get() != null) {
                Bundle data = message.getData();
                data.setClassLoader(MediaSessionCompat.class.getClassLoader());
                switch (message.what) {
                    case 1:
                        ((MediaBrowserServiceCallbackImpl) this.mCallbackImplRef.get()).onServiceConnected((Messenger) this.mCallbacksMessengerRef.get(), data.getString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID), (Token) data.getParcelable(MediaBrowserProtocol.DATA_MEDIA_SESSION_TOKEN), data.getBundle(MediaBrowserProtocol.DATA_ROOT_HINTS));
                        break;
                    case 2:
                        ((MediaBrowserServiceCallbackImpl) this.mCallbackImplRef.get()).onConnectionFailed((Messenger) this.mCallbacksMessengerRef.get());
                        break;
                    case 3:
                        ((MediaBrowserServiceCallbackImpl) this.mCallbackImplRef.get()).onLoadChildren((Messenger) this.mCallbacksMessengerRef.get(), data.getString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID), data.getParcelableArrayList(MediaBrowserProtocol.DATA_MEDIA_ITEM_LIST), data.getBundle(MediaBrowserProtocol.DATA_OPTIONS));
                        break;
                    default:
                        String str = MediaBrowserCompat.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unhandled message: ");
                        sb.append(message);
                        sb.append("\n  Client version: ");
                        sb.append(1);
                        sb.append("\n  Service version: ");
                        sb.append(message.arg1);
                        Log.w(str, sb.toString());
                        break;
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void setCallbacksMessenger(Messenger messenger) {
            this.mCallbacksMessengerRef = new WeakReference<>(messenger);
        }
    }

    public static class ConnectionCallback {
        ConnectionCallbackInternal mConnectionCallbackInternal;
        final Object mConnectionCallbackObj;

        interface ConnectionCallbackInternal {
            void onConnected();

            void onConnectionFailed();

            void onConnectionSuspended();
        }

        private class StubApi21 implements ConnectionCallback {
            StubApi21() {
            }

            public void onConnected() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnected();
                }
                ConnectionCallback.this.onConnected();
            }

            public void onConnectionSuspended() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnectionSuspended();
                }
                ConnectionCallback.this.onConnectionSuspended();
            }

            public void onConnectionFailed() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnectionFailed();
                }
                ConnectionCallback.this.onConnectionFailed();
            }
        }

        public void onConnected() {
        }

        public void onConnectionFailed() {
        }

        public void onConnectionSuspended() {
        }

        public ConnectionCallback() {
            if (VERSION.SDK_INT >= 21) {
                this.mConnectionCallbackObj = MediaBrowserCompatApi21.createConnectionCallback(new StubApi21());
            } else {
                this.mConnectionCallbackObj = null;
            }
        }

        /* access modifiers changed from: 0000 */
        public void setInternalConnectionCallback(ConnectionCallbackInternal connectionCallbackInternal) {
            this.mConnectionCallbackInternal = connectionCallbackInternal;
        }
    }

    public static abstract class ItemCallback {
        final Object mItemCallbackObj;

        private class StubApi23 implements ItemCallback {
            StubApi23() {
            }

            public void onItemLoaded(Parcel parcel) {
                if (parcel == null) {
                    ItemCallback.this.onItemLoaded(null);
                    return;
                }
                parcel.setDataPosition(0);
                MediaItem mediaItem = (MediaItem) MediaItem.CREATOR.createFromParcel(parcel);
                parcel.recycle();
                ItemCallback.this.onItemLoaded(mediaItem);
            }

            public void onError(@NonNull String str) {
                ItemCallback.this.onError(str);
            }
        }

        public void onError(@NonNull String str) {
        }

        public void onItemLoaded(MediaItem mediaItem) {
        }

        public ItemCallback() {
            if (VERSION.SDK_INT >= 23) {
                this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback(new StubApi23());
            } else {
                this.mItemCallbackObj = null;
            }
        }
    }

    private static class ItemReceiver extends ResultReceiver {
        private final ItemCallback mCallback;
        private final String mMediaId;

        ItemReceiver(String str, ItemCallback itemCallback, Handler handler) {
            super(handler);
            this.mMediaId = str;
            this.mCallback = itemCallback;
        }

        /* access modifiers changed from: protected */
        public void onReceiveResult(int i, Bundle bundle) {
            if (bundle != null) {
                bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            }
            if (i != 0 || bundle == null || !bundle.containsKey(MediaBrowserServiceCompat.KEY_MEDIA_ITEM)) {
                this.mCallback.onError(this.mMediaId);
                return;
            }
            Parcelable parcelable = bundle.getParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM);
            if (parcelable == null || (parcelable instanceof MediaItem)) {
                this.mCallback.onItemLoaded((MediaItem) parcelable);
            } else {
                this.mCallback.onError(this.mMediaId);
            }
        }
    }

    interface MediaBrowserImpl {
        void connect();

        void disconnect();

        @Nullable
        Bundle getExtras();

        void getItem(@NonNull String str, @NonNull ItemCallback itemCallback);

        @NonNull
        String getRoot();

        ComponentName getServiceComponent();

        @NonNull
        Token getSessionToken();

        boolean isConnected();

        void search(@NonNull String str, Bundle bundle, @NonNull SearchCallback searchCallback);

        void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback);

        void unsubscribe(@NonNull String str, SubscriptionCallback subscriptionCallback);
    }

    static class MediaBrowserImplApi21 implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl, ConnectionCallbackInternal {
        protected final Object mBrowserObj;
        protected Messenger mCallbacksMessenger;
        protected final CallbackHandler mHandler = new CallbackHandler(this);
        protected final Bundle mRootHints;
        protected ServiceBinderWrapper mServiceBinderWrapper;
        private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap<>();

        public void onConnectionFailed() {
        }

        public void onConnectionFailed(Messenger messenger) {
        }

        public void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle) {
        }

        public MediaBrowserImplApi21(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            Bundle bundle2;
            if (VERSION.SDK_INT <= 25) {
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putInt(MediaBrowserProtocol.EXTRA_CLIENT_VERSION, 1);
                this.mRootHints = new Bundle(bundle);
            } else {
                if (bundle == null) {
                    bundle2 = null;
                } else {
                    bundle2 = new Bundle(bundle);
                }
                this.mRootHints = bundle2;
            }
            connectionCallback.setInternalConnectionCallback(this);
            this.mBrowserObj = MediaBrowserCompatApi21.createBrowser(context, componentName, connectionCallback.mConnectionCallbackObj, this.mRootHints);
        }

        public void connect() {
            MediaBrowserCompatApi21.connect(this.mBrowserObj);
        }

        public void disconnect() {
            if (!(this.mServiceBinderWrapper == null || this.mCallbacksMessenger == null)) {
                try {
                    this.mServiceBinderWrapper.unregisterCallbackMessenger(this.mCallbacksMessenger);
                } catch (RemoteException unused) {
                    Log.i(MediaBrowserCompat.TAG, "Remote error unregistering client messenger.");
                }
            }
            MediaBrowserCompatApi21.disconnect(this.mBrowserObj);
        }

        public boolean isConnected() {
            return MediaBrowserCompatApi21.isConnected(this.mBrowserObj);
        }

        public ComponentName getServiceComponent() {
            return MediaBrowserCompatApi21.getServiceComponent(this.mBrowserObj);
        }

        @NonNull
        public String getRoot() {
            return MediaBrowserCompatApi21.getRoot(this.mBrowserObj);
        }

        @Nullable
        public Bundle getExtras() {
            return MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
        }

        @NonNull
        public Token getSessionToken() {
            return Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj));
        }

        public void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
            Bundle bundle2;
            Subscription subscription = (Subscription) this.mSubscriptions.get(str);
            if (subscription == null) {
                subscription = new Subscription();
                this.mSubscriptions.put(str, subscription);
            }
            subscriptionCallback.setSubscription(subscription);
            if (bundle == null) {
                bundle2 = null;
            } else {
                bundle2 = new Bundle(bundle);
            }
            subscription.putCallback(bundle2, subscriptionCallback);
            if (this.mServiceBinderWrapper == null) {
                MediaBrowserCompatApi21.subscribe(this.mBrowserObj, str, subscriptionCallback.mSubscriptionCallbackObj);
                return;
            }
            try {
                this.mServiceBinderWrapper.addSubscription(str, subscriptionCallback.mToken, bundle2, this.mCallbacksMessenger);
            } catch (RemoteException unused) {
                String str2 = MediaBrowserCompat.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Remote error subscribing media item: ");
                sb.append(str);
                Log.i(str2, sb.toString());
            }
        }

        public void unsubscribe(@NonNull String str, SubscriptionCallback subscriptionCallback) {
            Subscription subscription = (Subscription) this.mSubscriptions.get(str);
            if (subscription != null) {
                if (this.mServiceBinderWrapper == null) {
                    if (subscriptionCallback == null) {
                        MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, str);
                    } else {
                        List callbacks = subscription.getCallbacks();
                        List optionsList = subscription.getOptionsList();
                        for (int size = callbacks.size() - 1; size >= 0; size--) {
                            if (callbacks.get(size) == subscriptionCallback) {
                                callbacks.remove(size);
                                optionsList.remove(size);
                            }
                        }
                        if (callbacks.size() == 0) {
                            MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, str);
                        }
                    }
                } else if (subscriptionCallback == null) {
                    try {
                        this.mServiceBinderWrapper.removeSubscription(str, null, this.mCallbacksMessenger);
                    } catch (RemoteException unused) {
                        String str2 = MediaBrowserCompat.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("removeSubscription failed with RemoteException parentId=");
                        sb.append(str);
                        Log.d(str2, sb.toString());
                    }
                } else {
                    List callbacks2 = subscription.getCallbacks();
                    List optionsList2 = subscription.getOptionsList();
                    for (int size2 = callbacks2.size() - 1; size2 >= 0; size2--) {
                        if (callbacks2.get(size2) == subscriptionCallback) {
                            this.mServiceBinderWrapper.removeSubscription(str, subscriptionCallback.mToken, this.mCallbacksMessenger);
                            callbacks2.remove(size2);
                            optionsList2.remove(size2);
                        }
                    }
                }
                if (subscription.isEmpty() || subscriptionCallback == null) {
                    this.mSubscriptions.remove(str);
                }
            }
        }

        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("mediaId is empty");
            } else if (itemCallback == null) {
                throw new IllegalArgumentException("cb is null");
            } else if (!MediaBrowserCompatApi21.isConnected(this.mBrowserObj)) {
                Log.i(MediaBrowserCompat.TAG, "Not connected, unable to retrieve the MediaItem.");
                this.mHandler.post(new Runnable() {
                    public void run() {
                        itemCallback.onError(str);
                    }
                });
            } else if (this.mServiceBinderWrapper == null) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        itemCallback.onError(str);
                    }
                });
            } else {
                try {
                    this.mServiceBinderWrapper.getMediaItem(str, new ItemReceiver(str, itemCallback, this.mHandler), this.mCallbacksMessenger);
                } catch (RemoteException unused) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Remote error getting media item: ");
                    sb.append(str);
                    Log.i(str2, sb.toString());
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            itemCallback.onError(str);
                        }
                    });
                }
            }
        }

        public void search(@NonNull final String str, final Bundle bundle, @NonNull final SearchCallback searchCallback) {
            if (!isConnected()) {
                Log.i(MediaBrowserCompat.TAG, "Not connected, unable to search.");
                this.mHandler.post(new Runnable() {
                    public void run() {
                        searchCallback.onError(str, bundle);
                    }
                });
            } else if (this.mServiceBinderWrapper == null) {
                Log.i(MediaBrowserCompat.TAG, "The connected service doesn't support search.");
                this.mHandler.post(new Runnable() {
                    public void run() {
                        searchCallback.onError(str, bundle);
                    }
                });
            } else {
                try {
                    this.mServiceBinderWrapper.search(str, bundle, new SearchResultReceiver(str, bundle, searchCallback, this.mHandler), this.mCallbacksMessenger);
                } catch (RemoteException e) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Remote error searching items with query: ");
                    sb.append(str);
                    Log.i(str2, sb.toString(), e);
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            searchCallback.onError(str, bundle);
                        }
                    });
                }
            }
        }

        public void onConnected() {
            Bundle extras = MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
            if (extras != null) {
                IBinder binder = BundleCompat.getBinder(extras, MediaBrowserProtocol.EXTRA_MESSENGER_BINDER);
                if (binder != null) {
                    this.mServiceBinderWrapper = new ServiceBinderWrapper(binder, this.mRootHints);
                    this.mCallbacksMessenger = new Messenger(this.mHandler);
                    this.mHandler.setCallbacksMessenger(this.mCallbacksMessenger);
                    try {
                        this.mServiceBinderWrapper.registerCallbackMessenger(this.mCallbacksMessenger);
                    } catch (RemoteException unused) {
                        Log.i(MediaBrowserCompat.TAG, "Remote error registering client messenger.");
                    }
                }
            }
        }

        public void onConnectionSuspended() {
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mHandler.setCallbacksMessenger(null);
        }

        public void onLoadChildren(Messenger messenger, String str, List list, Bundle bundle) {
            if (this.mCallbacksMessenger == messenger) {
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription == null) {
                    if (MediaBrowserCompat.DEBUG) {
                        String str2 = MediaBrowserCompat.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onLoadChildren for id that isn't subscribed id=");
                        sb.append(str);
                        Log.d(str2, sb.toString());
                    }
                    return;
                }
                SubscriptionCallback callback = subscription.getCallback(bundle);
                if (callback != null) {
                    if (bundle == null) {
                        if (list == null) {
                            callback.onError(str);
                        } else {
                            callback.onChildrenLoaded(str, list);
                        }
                    } else if (list == null) {
                        callback.onError(str, bundle);
                    } else {
                        callback.onChildrenLoaded(str, list, bundle);
                    }
                }
            }
        }
    }

    static class MediaBrowserImplApi23 extends MediaBrowserImplApi21 {
        public MediaBrowserImplApi23(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            super(context, componentName, connectionCallback, bundle);
        }

        public void getItem(@NonNull String str, @NonNull ItemCallback itemCallback) {
            if (this.mServiceBinderWrapper == null) {
                MediaBrowserCompatApi23.getItem(this.mBrowserObj, str, itemCallback.mItemCallbackObj);
            } else {
                super.getItem(str, itemCallback);
            }
        }
    }

    static class MediaBrowserImplApi24 extends MediaBrowserImplApi23 {
        public MediaBrowserImplApi24(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            super(context, componentName, connectionCallback, bundle);
        }

        public void subscribe(@NonNull String str, @NonNull Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
            if (bundle == null) {
                MediaBrowserCompatApi21.subscribe(this.mBrowserObj, str, subscriptionCallback.mSubscriptionCallbackObj);
            } else {
                MediaBrowserCompatApi24.subscribe(this.mBrowserObj, str, bundle, subscriptionCallback.mSubscriptionCallbackObj);
            }
        }

        public void unsubscribe(@NonNull String str, SubscriptionCallback subscriptionCallback) {
            if (subscriptionCallback == null) {
                MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, str);
            } else {
                MediaBrowserCompatApi24.unsubscribe(this.mBrowserObj, str, subscriptionCallback.mSubscriptionCallbackObj);
            }
        }
    }

    static class MediaBrowserImplBase implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl {
        private static final int CONNECT_STATE_CONNECTED = 2;
        static final int CONNECT_STATE_CONNECTING = 1;
        static final int CONNECT_STATE_DISCONNECTED = 0;
        static final int CONNECT_STATE_SUSPENDED = 3;
        final ConnectionCallback mCallback;
        Messenger mCallbacksMessenger;
        final Context mContext;
        private Bundle mExtras;
        final CallbackHandler mHandler = new CallbackHandler(this);
        private Token mMediaSessionToken;
        final Bundle mRootHints;
        private String mRootId;
        ServiceBinderWrapper mServiceBinderWrapper;
        final ComponentName mServiceComponent;
        MediaServiceConnection mServiceConnection;
        int mState = 0;
        private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap<>();

        private class MediaServiceConnection implements ServiceConnection {
            MediaServiceConnection() {
            }

            public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
                postOrRun(new Runnable() {
                    public void run() {
                        if (MediaBrowserCompat.DEBUG) {
                            String str = MediaBrowserCompat.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("MediaServiceConnection.onServiceConnected name=");
                            sb.append(componentName);
                            sb.append(" binder=");
                            sb.append(iBinder);
                            Log.d(str, sb.toString());
                            MediaBrowserImplBase.this.dump();
                        }
                        if (MediaServiceConnection.this.isCurrent("onServiceConnected")) {
                            MediaBrowserImplBase.this.mServiceBinderWrapper = new ServiceBinderWrapper(iBinder, MediaBrowserImplBase.this.mRootHints);
                            MediaBrowserImplBase.this.mCallbacksMessenger = new Messenger(MediaBrowserImplBase.this.mHandler);
                            MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(MediaBrowserImplBase.this.mCallbacksMessenger);
                            MediaBrowserImplBase.this.mState = 1;
                            try {
                                if (MediaBrowserCompat.DEBUG) {
                                    Log.d(MediaBrowserCompat.TAG, "ServiceCallbacks.onConnect...");
                                    MediaBrowserImplBase.this.dump();
                                }
                                MediaBrowserImplBase.this.mServiceBinderWrapper.connect(MediaBrowserImplBase.this.mContext, MediaBrowserImplBase.this.mCallbacksMessenger);
                            } catch (RemoteException unused) {
                                String str2 = MediaBrowserCompat.TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("RemoteException during connect for ");
                                sb2.append(MediaBrowserImplBase.this.mServiceComponent);
                                Log.w(str2, sb2.toString());
                                if (MediaBrowserCompat.DEBUG) {
                                    Log.d(MediaBrowserCompat.TAG, "ServiceCallbacks.onConnect...");
                                    MediaBrowserImplBase.this.dump();
                                }
                            }
                        }
                    }
                });
            }

            public void onServiceDisconnected(final ComponentName componentName) {
                postOrRun(new Runnable() {
                    public void run() {
                        if (MediaBrowserCompat.DEBUG) {
                            String str = MediaBrowserCompat.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("MediaServiceConnection.onServiceDisconnected name=");
                            sb.append(componentName);
                            sb.append(" this=");
                            sb.append(this);
                            sb.append(" mServiceConnection=");
                            sb.append(MediaBrowserImplBase.this.mServiceConnection);
                            Log.d(str, sb.toString());
                            MediaBrowserImplBase.this.dump();
                        }
                        if (MediaServiceConnection.this.isCurrent("onServiceDisconnected")) {
                            MediaBrowserImplBase.this.mServiceBinderWrapper = null;
                            MediaBrowserImplBase.this.mCallbacksMessenger = null;
                            MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(null);
                            MediaBrowserImplBase.this.mState = 3;
                            MediaBrowserImplBase.this.mCallback.onConnectionSuspended();
                        }
                    }
                });
            }

            private void postOrRun(Runnable runnable) {
                if (Thread.currentThread() == MediaBrowserImplBase.this.mHandler.getLooper().getThread()) {
                    runnable.run();
                } else {
                    MediaBrowserImplBase.this.mHandler.post(runnable);
                }
            }

            /* access modifiers changed from: 0000 */
            public boolean isCurrent(String str) {
                if (MediaBrowserImplBase.this.mServiceConnection == this) {
                    return true;
                }
                if (MediaBrowserImplBase.this.mState != 0) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(" for ");
                    sb.append(MediaBrowserImplBase.this.mServiceComponent);
                    sb.append(" with mServiceConnection=");
                    sb.append(MediaBrowserImplBase.this.mServiceConnection);
                    sb.append(" this=");
                    sb.append(this);
                    Log.i(str2, sb.toString());
                }
                return false;
            }
        }

        public MediaBrowserImplBase(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            Bundle bundle2;
            if (context == null) {
                throw new IllegalArgumentException("context must not be null");
            } else if (componentName == null) {
                throw new IllegalArgumentException("service component must not be null");
            } else if (connectionCallback != null) {
                this.mContext = context;
                this.mServiceComponent = componentName;
                this.mCallback = connectionCallback;
                if (bundle == null) {
                    bundle2 = null;
                } else {
                    bundle2 = new Bundle(bundle);
                }
                this.mRootHints = bundle2;
            } else {
                throw new IllegalArgumentException("connection callback must not be null");
            }
        }

        public void connect() {
            boolean z;
            if (this.mState != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("connect() called while not disconnected (state=");
                sb.append(getStateLabel(this.mState));
                sb.append(")");
                throw new IllegalStateException(sb.toString());
            } else if (MediaBrowserCompat.DEBUG && this.mServiceConnection != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("mServiceConnection should be null. Instead it is ");
                sb2.append(this.mServiceConnection);
                throw new RuntimeException(sb2.toString());
            } else if (this.mServiceBinderWrapper != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("mServiceBinderWrapper should be null. Instead it is ");
                sb3.append(this.mServiceBinderWrapper);
                throw new RuntimeException(sb3.toString());
            } else if (this.mCallbacksMessenger == null) {
                this.mState = 1;
                Intent intent = new Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE);
                intent.setComponent(this.mServiceComponent);
                final MediaServiceConnection mediaServiceConnection = new MediaServiceConnection();
                this.mServiceConnection = mediaServiceConnection;
                try {
                    z = this.mContext.bindService(intent, this.mServiceConnection, 1);
                } catch (Exception unused) {
                    String str = MediaBrowserCompat.TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Failed binding to service ");
                    sb4.append(this.mServiceComponent);
                    Log.e(str, sb4.toString());
                    z = false;
                }
                if (!z) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            if (mediaServiceConnection == MediaBrowserImplBase.this.mServiceConnection) {
                                MediaBrowserImplBase.this.forceCloseConnection();
                                MediaBrowserImplBase.this.mCallback.onConnectionFailed();
                            }
                        }
                    });
                }
                if (MediaBrowserCompat.DEBUG) {
                    Log.d(MediaBrowserCompat.TAG, "connect...");
                    dump();
                }
            } else {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("mCallbacksMessenger should be null. Instead it is ");
                sb5.append(this.mCallbacksMessenger);
                throw new RuntimeException(sb5.toString());
            }
        }

        public void disconnect() {
            if (this.mCallbacksMessenger != null) {
                try {
                    this.mServiceBinderWrapper.disconnect(this.mCallbacksMessenger);
                } catch (RemoteException unused) {
                    String str = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("RemoteException during connect for ");
                    sb.append(this.mServiceComponent);
                    Log.w(str, sb.toString());
                }
            }
            forceCloseConnection();
            if (MediaBrowserCompat.DEBUG) {
                Log.d(MediaBrowserCompat.TAG, "disconnect...");
                dump();
            }
        }

        /* access modifiers changed from: 0000 */
        public void forceCloseConnection() {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService(this.mServiceConnection);
            }
            this.mState = 0;
            this.mServiceConnection = null;
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mHandler.setCallbacksMessenger(null);
            this.mRootId = null;
            this.mMediaSessionToken = null;
        }

        public boolean isConnected() {
            return this.mState == 2;
        }

        @NonNull
        public ComponentName getServiceComponent() {
            if (isConnected()) {
                return this.mServiceComponent;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getServiceComponent() called while not connected (state=");
            sb.append(this.mState);
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        @NonNull
        public String getRoot() {
            if (isConnected()) {
                return this.mRootId;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getRoot() called while not connected(state=");
            sb.append(getStateLabel(this.mState));
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        @Nullable
        public Bundle getExtras() {
            if (isConnected()) {
                return this.mExtras;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getExtras() called while not connected (state=");
            sb.append(getStateLabel(this.mState));
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        @NonNull
        public Token getSessionToken() {
            if (isConnected()) {
                return this.mMediaSessionToken;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getSessionToken() called while not connected(state=");
            sb.append(this.mState);
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        public void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
            Bundle bundle2;
            Subscription subscription = (Subscription) this.mSubscriptions.get(str);
            if (subscription == null) {
                subscription = new Subscription();
                this.mSubscriptions.put(str, subscription);
            }
            if (bundle == null) {
                bundle2 = null;
            } else {
                bundle2 = new Bundle(bundle);
            }
            subscription.putCallback(bundle2, subscriptionCallback);
            if (this.mState == 2) {
                try {
                    this.mServiceBinderWrapper.addSubscription(str, subscriptionCallback.mToken, bundle2, this.mCallbacksMessenger);
                } catch (RemoteException unused) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("addSubscription failed with RemoteException parentId=");
                    sb.append(str);
                    Log.d(str2, sb.toString());
                }
            }
        }

        public void unsubscribe(@NonNull String str, SubscriptionCallback subscriptionCallback) {
            Subscription subscription = (Subscription) this.mSubscriptions.get(str);
            if (subscription != null) {
                if (subscriptionCallback == null) {
                    try {
                        if (this.mState == 2) {
                            this.mServiceBinderWrapper.removeSubscription(str, null, this.mCallbacksMessenger);
                        }
                    } catch (RemoteException unused) {
                        String str2 = MediaBrowserCompat.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("removeSubscription failed with RemoteException parentId=");
                        sb.append(str);
                        Log.d(str2, sb.toString());
                    }
                } else {
                    List callbacks = subscription.getCallbacks();
                    List optionsList = subscription.getOptionsList();
                    for (int size = callbacks.size() - 1; size >= 0; size--) {
                        if (callbacks.get(size) == subscriptionCallback) {
                            if (this.mState == 2) {
                                this.mServiceBinderWrapper.removeSubscription(str, subscriptionCallback.mToken, this.mCallbacksMessenger);
                            }
                            callbacks.remove(size);
                            optionsList.remove(size);
                        }
                    }
                }
                if (subscription.isEmpty() || subscriptionCallback == null) {
                    this.mSubscriptions.remove(str);
                }
            }
        }

        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("mediaId is empty");
            } else if (itemCallback == null) {
                throw new IllegalArgumentException("cb is null");
            } else if (this.mState != 2) {
                Log.i(MediaBrowserCompat.TAG, "Not connected, unable to retrieve the MediaItem.");
                this.mHandler.post(new Runnable() {
                    public void run() {
                        itemCallback.onError(str);
                    }
                });
            } else {
                try {
                    this.mServiceBinderWrapper.getMediaItem(str, new ItemReceiver(str, itemCallback, this.mHandler), this.mCallbacksMessenger);
                } catch (RemoteException unused) {
                    Log.i(MediaBrowserCompat.TAG, "Remote error getting media item.");
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            itemCallback.onError(str);
                        }
                    });
                }
            }
        }

        public void search(@NonNull final String str, final Bundle bundle, @NonNull final SearchCallback searchCallback) {
            if (!isConnected()) {
                Log.i(MediaBrowserCompat.TAG, "Not connected, unable to search.");
                this.mHandler.post(new Runnable() {
                    public void run() {
                        searchCallback.onError(str, bundle);
                    }
                });
                return;
            }
            try {
                this.mServiceBinderWrapper.search(str, bundle, new SearchResultReceiver(str, bundle, searchCallback, this.mHandler), this.mCallbacksMessenger);
            } catch (RemoteException e) {
                String str2 = MediaBrowserCompat.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Remote error searching items with query: ");
                sb.append(str);
                Log.i(str2, sb.toString(), e);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        searchCallback.onError(str, bundle);
                    }
                });
            }
        }

        public void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle) {
            if (isCurrent(messenger, "onConnect")) {
                if (this.mState != 1) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onConnect from service while mState=");
                    sb.append(getStateLabel(this.mState));
                    sb.append("... ignoring");
                    Log.w(str2, sb.toString());
                    return;
                }
                this.mRootId = str;
                this.mMediaSessionToken = token;
                this.mExtras = bundle;
                this.mState = 2;
                if (MediaBrowserCompat.DEBUG) {
                    Log.d(MediaBrowserCompat.TAG, "ServiceCallbacks.onConnect...");
                    dump();
                }
                this.mCallback.onConnected();
                try {
                    for (Entry entry : this.mSubscriptions.entrySet()) {
                        String str3 = (String) entry.getKey();
                        Subscription subscription = (Subscription) entry.getValue();
                        List callbacks = subscription.getCallbacks();
                        List optionsList = subscription.getOptionsList();
                        for (int i = 0; i < callbacks.size(); i++) {
                            this.mServiceBinderWrapper.addSubscription(str3, ((SubscriptionCallback) callbacks.get(i)).mToken, (Bundle) optionsList.get(i), this.mCallbacksMessenger);
                        }
                    }
                } catch (RemoteException unused) {
                    Log.d(MediaBrowserCompat.TAG, "addSubscription failed with RemoteException.");
                }
            }
        }

        public void onConnectionFailed(Messenger messenger) {
            String str = MediaBrowserCompat.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onConnectFailed for ");
            sb.append(this.mServiceComponent);
            Log.e(str, sb.toString());
            if (isCurrent(messenger, "onConnectFailed")) {
                if (this.mState != 1) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("onConnect from service while mState=");
                    sb2.append(getStateLabel(this.mState));
                    sb2.append("... ignoring");
                    Log.w(str2, sb2.toString());
                    return;
                }
                forceCloseConnection();
                this.mCallback.onConnectionFailed();
            }
        }

        public void onLoadChildren(Messenger messenger, String str, List list, Bundle bundle) {
            if (isCurrent(messenger, "onLoadChildren")) {
                if (MediaBrowserCompat.DEBUG) {
                    String str2 = MediaBrowserCompat.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onLoadChildren for ");
                    sb.append(this.mServiceComponent);
                    sb.append(" id=");
                    sb.append(str);
                    Log.d(str2, sb.toString());
                }
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription == null) {
                    if (MediaBrowserCompat.DEBUG) {
                        String str3 = MediaBrowserCompat.TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("onLoadChildren for id that isn't subscribed id=");
                        sb2.append(str);
                        Log.d(str3, sb2.toString());
                    }
                    return;
                }
                SubscriptionCallback callback = subscription.getCallback(bundle);
                if (callback != null) {
                    if (bundle == null) {
                        if (list == null) {
                            callback.onError(str);
                        } else {
                            callback.onChildrenLoaded(str, list);
                        }
                    } else if (list == null) {
                        callback.onError(str, bundle);
                    } else {
                        callback.onChildrenLoaded(str, list, bundle);
                    }
                }
            }
        }

        private static String getStateLabel(int i) {
            switch (i) {
                case 0:
                    return "CONNECT_STATE_DISCONNECTED";
                case 1:
                    return "CONNECT_STATE_CONNECTING";
                case 2:
                    return "CONNECT_STATE_CONNECTED";
                case 3:
                    return "CONNECT_STATE_SUSPENDED";
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("UNKNOWN/");
                    sb.append(i);
                    return sb.toString();
            }
        }

        private boolean isCurrent(Messenger messenger, String str) {
            if (this.mCallbacksMessenger == messenger) {
                return true;
            }
            if (this.mState != 0) {
                String str2 = MediaBrowserCompat.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(" for ");
                sb.append(this.mServiceComponent);
                sb.append(" with mCallbacksMessenger=");
                sb.append(this.mCallbacksMessenger);
                sb.append(" this=");
                sb.append(this);
                Log.i(str2, sb.toString());
            }
            return false;
        }

        /* access modifiers changed from: 0000 */
        public void dump() {
            Log.d(MediaBrowserCompat.TAG, "MediaBrowserCompat...");
            String str = MediaBrowserCompat.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("  mServiceComponent=");
            sb.append(this.mServiceComponent);
            Log.d(str, sb.toString());
            String str2 = MediaBrowserCompat.TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  mCallback=");
            sb2.append(this.mCallback);
            Log.d(str2, sb2.toString());
            String str3 = MediaBrowserCompat.TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  mRootHints=");
            sb3.append(this.mRootHints);
            Log.d(str3, sb3.toString());
            String str4 = MediaBrowserCompat.TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  mState=");
            sb4.append(getStateLabel(this.mState));
            Log.d(str4, sb4.toString());
            String str5 = MediaBrowserCompat.TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  mServiceConnection=");
            sb5.append(this.mServiceConnection);
            Log.d(str5, sb5.toString());
            String str6 = MediaBrowserCompat.TAG;
            StringBuilder sb6 = new StringBuilder();
            sb6.append("  mServiceBinderWrapper=");
            sb6.append(this.mServiceBinderWrapper);
            Log.d(str6, sb6.toString());
            String str7 = MediaBrowserCompat.TAG;
            StringBuilder sb7 = new StringBuilder();
            sb7.append("  mCallbacksMessenger=");
            sb7.append(this.mCallbacksMessenger);
            Log.d(str7, sb7.toString());
            String str8 = MediaBrowserCompat.TAG;
            StringBuilder sb8 = new StringBuilder();
            sb8.append("  mRootId=");
            sb8.append(this.mRootId);
            Log.d(str8, sb8.toString());
            String str9 = MediaBrowserCompat.TAG;
            StringBuilder sb9 = new StringBuilder();
            sb9.append("  mMediaSessionToken=");
            sb9.append(this.mMediaSessionToken);
            Log.d(str9, sb9.toString());
        }
    }

    interface MediaBrowserServiceCallbackImpl {
        void onConnectionFailed(Messenger messenger);

        void onLoadChildren(Messenger messenger, String str, List list, Bundle bundle);

        void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle);
    }

    public static class MediaItem implements Parcelable {
        public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
            public MediaItem createFromParcel(Parcel parcel) {
                return new MediaItem(parcel);
            }

            public MediaItem[] newArray(int i) {
                return new MediaItem[i];
            }
        };
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;

        @RestrictTo({Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Flags {
        }

        public int describeContents() {
            return 0;
        }

        public static MediaItem fromMediaItem(Object obj) {
            if (obj == null || VERSION.SDK_INT < 21) {
                return null;
            }
            return new MediaItem(MediaDescriptionCompat.fromMediaDescription(MediaItem.getDescription(obj)), MediaItem.getFlags(obj));
        }

        public static List<MediaItem> fromMediaItemList(List<?> list) {
            if (list == null || VERSION.SDK_INT < 21) {
                return null;
            }
            ArrayList arrayList = new ArrayList(list.size());
            for (Object fromMediaItem : list) {
                arrayList.add(fromMediaItem(fromMediaItem));
            }
            return arrayList;
        }

        public MediaItem(@NonNull MediaDescriptionCompat mediaDescriptionCompat, int i) {
            if (mediaDescriptionCompat == null) {
                throw new IllegalArgumentException("description cannot be null");
            } else if (!TextUtils.isEmpty(mediaDescriptionCompat.getMediaId())) {
                this.mFlags = i;
                this.mDescription = mediaDescriptionCompat;
            } else {
                throw new IllegalArgumentException("description must have a non-empty media id");
            }
        }

        MediaItem(Parcel parcel) {
            this.mFlags = parcel.readInt();
            this.mDescription = (MediaDescriptionCompat) MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mFlags);
            this.mDescription.writeToParcel(parcel, i);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=");
            sb.append(this.mFlags);
            sb.append(", mDescription=");
            sb.append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean isBrowsable() {
            return (this.mFlags & 1) != 0;
        }

        public boolean isPlayable() {
            return (this.mFlags & 2) != 0;
        }

        @NonNull
        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }

        @Nullable
        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
    }

    public static abstract class SearchCallback {
        public void onError(@NonNull String str, Bundle bundle) {
        }

        public void onSearchResult(@NonNull String str, Bundle bundle, @NonNull List<MediaItem> list) {
        }
    }

    private static class SearchResultReceiver extends ResultReceiver {
        private final SearchCallback mCallback;
        private final Bundle mExtras;
        private final String mQuery;

        SearchResultReceiver(String str, Bundle bundle, SearchCallback searchCallback, Handler handler) {
            super(handler);
            this.mQuery = str;
            this.mExtras = bundle;
            this.mCallback = searchCallback;
        }

        /* access modifiers changed from: protected */
        public void onReceiveResult(int i, Bundle bundle) {
            if (i != 0 || bundle == null || !bundle.containsKey(MediaBrowserServiceCompat.KEY_SEARCH_RESULTS)) {
                this.mCallback.onError(this.mQuery, this.mExtras);
                return;
            }
            Parcelable[] parcelableArray = bundle.getParcelableArray(MediaBrowserServiceCompat.KEY_SEARCH_RESULTS);
            ArrayList arrayList = null;
            if (parcelableArray != null) {
                arrayList = new ArrayList();
                for (Parcelable parcelable : parcelableArray) {
                    arrayList.add((MediaItem) parcelable);
                }
            }
            this.mCallback.onSearchResult(this.mQuery, this.mExtras, arrayList);
        }
    }

    private static class ServiceBinderWrapper {
        private Messenger mMessenger;
        private Bundle mRootHints;

        public ServiceBinderWrapper(IBinder iBinder, Bundle bundle) {
            this.mMessenger = new Messenger(iBinder);
            this.mRootHints = bundle;
        }

        /* access modifiers changed from: 0000 */
        public void connect(Context context, Messenger messenger) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putString(MediaBrowserProtocol.DATA_PACKAGE_NAME, context.getPackageName());
            bundle.putBundle(MediaBrowserProtocol.DATA_ROOT_HINTS, this.mRootHints);
            sendRequest(1, bundle, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void disconnect(Messenger messenger) throws RemoteException {
            sendRequest(2, null, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void addSubscription(String str, IBinder iBinder, Bundle bundle, Messenger messenger) throws RemoteException {
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            BundleCompat.putBinder(bundle2, MediaBrowserProtocol.DATA_CALLBACK_TOKEN, iBinder);
            bundle2.putBundle(MediaBrowserProtocol.DATA_OPTIONS, bundle);
            sendRequest(3, bundle2, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void removeSubscription(String str, IBinder iBinder, Messenger messenger) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            BundleCompat.putBinder(bundle, MediaBrowserProtocol.DATA_CALLBACK_TOKEN, iBinder);
            sendRequest(4, bundle, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void getMediaItem(String str, ResultReceiver resultReceiver, Messenger messenger) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            bundle.putParcelable(MediaBrowserProtocol.DATA_RESULT_RECEIVER, resultReceiver);
            sendRequest(5, bundle, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void registerCallbackMessenger(Messenger messenger) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putBundle(MediaBrowserProtocol.DATA_ROOT_HINTS, this.mRootHints);
            sendRequest(6, bundle, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void unregisterCallbackMessenger(Messenger messenger) throws RemoteException {
            sendRequest(7, null, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void search(String str, Bundle bundle, ResultReceiver resultReceiver, Messenger messenger) throws RemoteException {
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_SEARCH_QUERY, str);
            bundle2.putBundle(MediaBrowserProtocol.DATA_SEARCH_EXTRAS, bundle);
            bundle2.putParcelable(MediaBrowserProtocol.DATA_RESULT_RECEIVER, resultReceiver);
            sendRequest(8, bundle2, messenger);
        }

        private void sendRequest(int i, Bundle bundle, Messenger messenger) throws RemoteException {
            Message obtain = Message.obtain();
            obtain.what = i;
            obtain.arg1 = 1;
            obtain.setData(bundle);
            obtain.replyTo = messenger;
            this.mMessenger.send(obtain);
        }
    }

    private static class Subscription {
        private final List<SubscriptionCallback> mCallbacks = new ArrayList();
        private final List<Bundle> mOptionsList = new ArrayList();

        public boolean isEmpty() {
            return this.mCallbacks.isEmpty();
        }

        public List<Bundle> getOptionsList() {
            return this.mOptionsList;
        }

        public List<SubscriptionCallback> getCallbacks() {
            return this.mCallbacks;
        }

        public SubscriptionCallback getCallback(Bundle bundle) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), bundle)) {
                    return (SubscriptionCallback) this.mCallbacks.get(i);
                }
            }
            return null;
        }

        public void putCallback(Bundle bundle, SubscriptionCallback subscriptionCallback) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), bundle)) {
                    this.mCallbacks.set(i, subscriptionCallback);
                    return;
                }
            }
            this.mCallbacks.add(subscriptionCallback);
            this.mOptionsList.add(bundle);
        }
    }

    public static abstract class SubscriptionCallback {
        /* access modifiers changed from: private */
        public final Object mSubscriptionCallbackObj;
        WeakReference<Subscription> mSubscriptionRef;
        /* access modifiers changed from: private */
        public final IBinder mToken;

        private class StubApi21 implements SubscriptionCallback {
            StubApi21() {
            }

            public void onChildrenLoaded(@NonNull String str, List<?> list) {
                Subscription subscription = SubscriptionCallback.this.mSubscriptionRef == null ? null : (Subscription) SubscriptionCallback.this.mSubscriptionRef.get();
                if (subscription == null) {
                    SubscriptionCallback.this.onChildrenLoaded(str, MediaItem.fromMediaItemList(list));
                    return;
                }
                List fromMediaItemList = MediaItem.fromMediaItemList(list);
                List callbacks = subscription.getCallbacks();
                List optionsList = subscription.getOptionsList();
                for (int i = 0; i < callbacks.size(); i++) {
                    Bundle bundle = (Bundle) optionsList.get(i);
                    if (bundle == null) {
                        SubscriptionCallback.this.onChildrenLoaded(str, fromMediaItemList);
                    } else {
                        SubscriptionCallback.this.onChildrenLoaded(str, applyOptions(fromMediaItemList, bundle), bundle);
                    }
                }
            }

            public void onError(@NonNull String str) {
                SubscriptionCallback.this.onError(str);
            }

            /* access modifiers changed from: 0000 */
            public List<MediaItem> applyOptions(List<MediaItem> list, Bundle bundle) {
                if (list == null) {
                    return null;
                }
                int i = bundle.getInt(MediaBrowserCompat.EXTRA_PAGE, -1);
                int i2 = bundle.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1);
                if (i == -1 && i2 == -1) {
                    return list;
                }
                int i3 = i2 * i;
                int i4 = i3 + i2;
                if (i < 0 || i2 < 1 || i3 >= list.size()) {
                    return Collections.EMPTY_LIST;
                }
                if (i4 > list.size()) {
                    i4 = list.size();
                }
                return list.subList(i3, i4);
            }
        }

        private class StubApi24 extends StubApi21 implements SubscriptionCallback {
            StubApi24() {
                super();
            }

            public void onChildrenLoaded(@NonNull String str, List<?> list, @NonNull Bundle bundle) {
                SubscriptionCallback.this.onChildrenLoaded(str, MediaItem.fromMediaItemList(list), bundle);
            }

            public void onError(@NonNull String str, @NonNull Bundle bundle) {
                SubscriptionCallback.this.onError(str, bundle);
            }
        }

        public void onChildrenLoaded(@NonNull String str, @NonNull List<MediaItem> list) {
        }

        public void onChildrenLoaded(@NonNull String str, @NonNull List<MediaItem> list, @NonNull Bundle bundle) {
        }

        public void onError(@NonNull String str) {
        }

        public void onError(@NonNull String str, @NonNull Bundle bundle) {
        }

        public SubscriptionCallback() {
            if (VERSION.SDK_INT >= 26 || BuildCompat.isAtLeastO()) {
                this.mSubscriptionCallbackObj = MediaBrowserCompatApi24.createSubscriptionCallback(new StubApi24());
                this.mToken = null;
            } else if (VERSION.SDK_INT >= 21) {
                this.mSubscriptionCallbackObj = MediaBrowserCompatApi21.createSubscriptionCallback(new StubApi21());
                this.mToken = new Binder();
            } else {
                this.mSubscriptionCallbackObj = null;
                this.mToken = new Binder();
            }
        }

        /* access modifiers changed from: private */
        public void setSubscription(Subscription subscription) {
            this.mSubscriptionRef = new WeakReference<>(subscription);
        }
    }

    public MediaBrowserCompat(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
        if (VERSION.SDK_INT >= 26 || BuildCompat.isAtLeastO()) {
            this.mImpl = new MediaBrowserImplApi24(context, componentName, connectionCallback, bundle);
        } else if (VERSION.SDK_INT >= 23) {
            this.mImpl = new MediaBrowserImplApi23(context, componentName, connectionCallback, bundle);
        } else if (VERSION.SDK_INT >= 21) {
            this.mImpl = new MediaBrowserImplApi21(context, componentName, connectionCallback, bundle);
        } else {
            this.mImpl = new MediaBrowserImplBase(context, componentName, connectionCallback, bundle);
        }
    }

    public void connect() {
        this.mImpl.connect();
    }

    public void disconnect() {
        this.mImpl.disconnect();
    }

    public boolean isConnected() {
        return this.mImpl.isConnected();
    }

    @NonNull
    public ComponentName getServiceComponent() {
        return this.mImpl.getServiceComponent();
    }

    @NonNull
    public String getRoot() {
        return this.mImpl.getRoot();
    }

    @Nullable
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }

    @NonNull
    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public void subscribe(@NonNull String str, @NonNull SubscriptionCallback subscriptionCallback) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("parentId is empty");
        } else if (subscriptionCallback != null) {
            this.mImpl.subscribe(str, null, subscriptionCallback);
        } else {
            throw new IllegalArgumentException("callback is null");
        }
    }

    public void subscribe(@NonNull String str, @NonNull Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("parentId is empty");
        } else if (subscriptionCallback == null) {
            throw new IllegalArgumentException("callback is null");
        } else if (bundle != null) {
            this.mImpl.subscribe(str, bundle, subscriptionCallback);
        } else {
            throw new IllegalArgumentException("options are null");
        }
    }

    public void unsubscribe(@NonNull String str) {
        if (!TextUtils.isEmpty(str)) {
            this.mImpl.unsubscribe(str, null);
            return;
        }
        throw new IllegalArgumentException("parentId is empty");
    }

    public void unsubscribe(@NonNull String str, @NonNull SubscriptionCallback subscriptionCallback) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("parentId is empty");
        } else if (subscriptionCallback != null) {
            this.mImpl.unsubscribe(str, subscriptionCallback);
        } else {
            throw new IllegalArgumentException("callback is null");
        }
    }

    public void getItem(@NonNull String str, @NonNull ItemCallback itemCallback) {
        this.mImpl.getItem(str, itemCallback);
    }

    public void search(@NonNull String str, Bundle bundle, @NonNull SearchCallback searchCallback) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("query cannot be empty");
        } else if (searchCallback != null) {
            this.mImpl.search(str, bundle, searchCallback);
        } else {
            throw new IllegalArgumentException("callback cannot be null");
        }
    }
}
