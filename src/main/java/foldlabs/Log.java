package foldlabs;

public interface Log {
    void log(String message);

    class SystemLog implements Log {
        @Override
        public void log(String message) {
            System.out.println(message);
        }
    }
}
