import java.sql.Connection;
import java.util.Scanner;

public class ExecuteInterface {
    static Connection connection;
    static MyDatabase db;
    //static final String cmd[] = new String[]{"c","d","a","oya","tw","pd","calld"};
    static String[] cmd;

    public ExecuteInterface(){
        try {
            db = new MyDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
         cmd = new String[]{"c", "d", "a", "oya", "tw", "pd", "calld"};
    }

    public void runInterface() {
        Scanner scan = new Scanner(System.in);
        System.out.print("cmd> ");
        String input = scan.nextLine();

        while (input != null && !input.equals("q")) {
            String str[] = input.split("\\s+");
            String para = "";
            if (input.indexOf(" ") > 0) {
                para = input.substring(input.indexOf(" ")).trim();
            }

            String incmd = str[0];
            switch(incmd) {
                case cmd[0]:
                    db.allCountries();
                    break;
                case cmd[1]:

            }
        }
    }
}
