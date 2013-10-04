package foldlabs;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Path;
import java.util.Map;

public class LeinMatchers {
    static Matcher<String> aStringMatching(final String regex) {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String s) {
                return s.matches(regex);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a string matching " + regex);
            }
        };
    }

    static Matcher<Map<String, String>> aMapWith(final String key, final Matcher<String> valueMatcher) {
        return new TypeSafeMatcher<Map<String, String>>() {
            @Override
            protected boolean matchesSafely(Map<String, String> stringStringMap) {
                for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                    if(entry.getKey().equals(key) && valueMatcher.matches(entry.getValue()))
                        return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a map containing '" + key + "' with value s.t. " + valueMatcher);
            }
        };
    }

    static Matcher<Path> aPathMatching(final String regex) {
        return new TypeSafeMatcher<Path>() {
            @Override
            protected boolean matchesSafely(Path path) {
                return path.toString().matches(regex);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a path matching " + regex);
            }
        };
    }

    static Matcher<Path> aPathContaining(final String content) {
        return new TypeSafeMatcher<Path>() {
            @Override
            protected boolean matchesSafely(Path path) {
                return path.toString().contains(content);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a path containing " + content);
            }
        };
    }
}
