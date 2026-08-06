package simplev.common;

public class RunPrint implements Case {
    String str = "";

    public RunPrint(String str) {
        this.str = str;
    }

    @Override
    public Thread makeThread(boolean benchmark, boolean zen) {
        return new Thread(() -> {
            System.out.println(str);
        }, "RunPrintThread");
    }
}