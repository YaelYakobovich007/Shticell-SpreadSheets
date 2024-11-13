package util.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.function.Consumer;

public class SimpleCookieManager implements CookieJar {

    private final static String CACHE_MANAGER_PREFIX = "    [Cookie Manager] ---> ";
    Map<String, Map<String, Cookie>> cookies = new HashMap<>();
    private final Consumer<String> logData = System.out::println;

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        String host = httpUrl.host();
        List<Cookie> cookiesPerDomain = Collections.emptyList();
        synchronized (this) {
            if (cookies.containsKey(host)) {
                cookiesPerDomain = new ArrayList<>(cookies.get(host).values());
            }
        }
        return cookiesPerDomain;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> responseCookies) {
        String host = httpUrl.host();
        synchronized (this) {
            Map<String, Cookie> cookiesMap = cookies.computeIfAbsent(host, key -> new HashMap<>());
            responseCookies
                    .stream()
                    .filter(cookie -> !cookiesMap.containsKey(cookie.name()))
                    .forEach(cookie -> {
                        logData.accept(CACHE_MANAGER_PREFIX + "Storing cookie [" + cookie.name() + "] --> [" + cookie.value() + "]");
                        cookiesMap.put(cookie.name(), cookie);
                    });
        }
    }

    public void removeCookiesOf(String domain) {
        synchronized (this) {
            cookies.remove(domain);
        }
    }
}