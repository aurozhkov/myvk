package com.aurozhkov.vkwall.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aurozhkov.vkwall.R;
import com.aurozhkov.vkwall.base.VkFragment;
import com.aurozhkov.vkwall.data.UserInfoStore;
import com.aurozhkov.vkwall.events.LoginSuccessEvent;
import com.aurozhkov.vkwall.events.RetryNetworkRequestEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.InjectView;

public class LoginFragment extends VkFragment {

    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";

    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_ID = "user_id";

    @Inject UserInfoStore userInfoStore;

    @InjectView(R.id.login_loadable_card_layout) LoadableCardLayout loadableCardLayout;
    @InjectView(R.id.login_web_view) WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new LoginWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        loadLoginUrl();
    }

    @SuppressWarnings("UnusedDeclaration") // Used by Otto
    @Subscribe
    public void onRetryNetworkRequest(RetryNetworkRequestEvent event) {
        loadLoginUrl();
    }

    private void loadLoginUrl() {
        loadableCardLayout.showContent();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://oauth.vk.com/authorize?")
                .append("client_id=").append("4936872")
                .append("&scope=").append("wall")
                .append("&redirect_uri=").append(REDIRECT_URL)
                .append("&display=").append("mobile")
                .append("&v=").append("5.33")
                .append("&response_type=").append("token");
        webView.loadUrl(stringBuilder.toString());
    }

    private class LoginWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return url.startsWith(REDIRECT_URL);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url.startsWith(REDIRECT_URL)) {
                if (url.contains("#") && !url.contains("error")) {
                    String loginData = url.substring(url.indexOf('#') + 1);
                    String[] queryParams = loginData.split("&");
                    for (String queryParam : queryParams) {
                        String[] paramAndValue = queryParam.split("=");
                        if (ACCESS_TOKEN.equals(paramAndValue[0])) {
                            userInfoStore.saveAccessToken(paramAndValue[1]);
                        } else if (USER_ID.equals(paramAndValue[0])) {
                            userInfoStore.saveUserId(Long.parseLong(paramAndValue[1]));
                        }
                    }

                    webView.clearCache(true);
                    CookieSyncManager.createInstance(getActivity());
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookie();

                    bus.post(new LoginSuccessEvent());
                } else {
                    loadableCardLayout.showError();
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadableCardLayout.showError();
        }
    }
}
