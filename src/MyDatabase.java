import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDatabase {
    private Connection connection;
    private final String dbfile = "db_project.db";

    public static void main(String args[]) throws Exception{
        MyDatabase db = new MyDatabase();
//        db.allCountries();
//        db.allDisciplines();
//        db.athleteByCtryDis("Vietnam", "Swimming");
//        db.minMaxAthleteByDis(true,"Table Tennis");
//        db.topTenWomen();
//        db.GlobalPopularDis();
//        db.CountryPopularDis("Vietnam");
//        db.ctryTakeAllDis();

        String s = "abc";
        System.out.println(s.equals("abc")+"\n"+ (s == "abc"));
    }

    public MyDatabase() throws Exception {
        this.connection = DriverManager.getConnection("jdbc:sqlite:"+dbfile);
        System.out.println("Connection to "+dbfile+" is established.");
    }

    // 1.1 select all the countries (to let user know the country's spelling in our system)
    public void allCountries() {
        try {
            String qry = "select rowid, countryName from country order by rowid asc;";
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(qry);

            while(rs.next()) {
                System.out.println(rs.getInt("rowid") + " | " + rs.getString("countryName"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // 1.2 select all the disciplines (to let user know how many/the discipline's spelling in our system)
    public void allDisciplines() {
        try {
            String qry = "select rowid, discipline from discipline order by rowid asc;";
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(qry);

            while(rs.next()) {
                System.out.println(rs.getInt("rowid") + " | " + rs.getString("discipline"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // 1.3 select all athletes who are from the same country and play the same discipline
    public void athleteByCtryDis(String ctry, String dis) {
        try {
            String qry = "select a.name, c.countryName, d.discipline from athletes a" +
                        "join discipline d on a.discipline_code = d.discipline_code" +
                        "join country c on a.country_code = c.country_code" +
                        "where c.countryName = ? and d.discipline = ?;";
            PreparedStatement pstmt = this.connection.prepareStatement(qry);
            pstmt.setString(1,ctry);
            pstmt.setString(2,dis);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("name") + "\t|" +
                        rs.getString("countryName") + "\t|" +
                        rs.getString("discipline"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // 1.4 Find the oldest/youngest athlete that participated in a 'specific' discipline
    public void minMaxAthleteByDis(boolean oldest, String dis) {
        try {
            String qry = "";
            // oldest equivalent to min birthdate
            if (!oldest) {
                qry = "select name, max(birth_date) as birth_date, discipline from athletes a "+
                        "join discipline d on a.discipline_code = d.discipline_code "+
                        "where birth_date != '' and discipline = ?";
            } else {
                qry = "select name, min(birth_date) as birth_date, discipline from athletes a "+
                        "join discipline d on a.discipline_code = d.discipline_code "+
                        "where birth_date != '' and discipline = ?";
            }
            PreparedStatement pstmt = this.connection.prepareStatement(qry);
            pstmt.setString(1,dis);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("name") + "\t|" +
                        rs.getString("birth_date") + "\t|" +
                        rs.getString("discipline"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // 1.5 Find the top ten women that achieve the most medals, list their discipline and country
    public void topTenWomen() {
        try {
            String qry = "SELECT name, count(*) as medalCount, d.discipline, countryName as country" +
                    "from athletes a" +
                    "join discipline d on d.discipline_code = a.discipline_code" +
                    "join country c on a.country_code = c.country_code" +
                    "join wins w on a.name = w.athlete_name" +
                    "WHERE a.gender = \"Female\"" +
                    "GROUP by w.athlete_name" +
                    "ORDER by medalCount DESC" +
                    "LIMIT 10;";
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(qry);

            while(rs.next()) {
                System.out.println(rs.getString("name") + " \t\t| " + rs.getInt("medalCount")
                        + " \t| " + rs.getString("discipline")
                        + " \t| " + rs.getString("country"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    //

    // 2.3 Retrieve the discipline that is most popular, based on the number of countries play it
    public void GlobalPopularDis() {
        try {
            String qry = "SELECT d.discipline, count(DISTINCT c.country_code) as numOfCountry, count(a.name) as numOfAthlete" +
                        "FROM discipline d" +
                        "join athletes a on d.discipline_code  = a.discipline_code" +
                        "join country c on a.country_code = c.country_code" +
                        "GROUP by d.discipline_code" +
                        "ORDER by numOfCountry DESC;";

            PreparedStatement pstmt = this.connection.prepareStatement(qry);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("discipline") + "\t|" +
                        rs.getString("numOfCountry") + "\t|" +
                        rs.getString("numOfAthlete"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // 2.3' Similar to 2.3 but popular within a country, based on the number of athletes play it
    public void CountryPopularDis(String ctry) {
        try {
            String qry = "SELECT d.discipline, count(a.name) as numOfAthlete" +
                        "FROM discipline d" +
                        "join athletes a on d.discipline_code  = a.discipline_code" +
                        "join country c on a.country_code = c.country_code" +
                        "WHERE countryName = ?" +
                        "GROUP by d.discipline_code" +
                        "ORDER by numOfAthlete DESC;";

            PreparedStatement pstmt = this.connection.prepareStatement(qry);
            pstmt.setString(1,ctry);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("discipline") + "\t|" +
                        rs.getString("numOfAthlete"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // 3.1 List all countries that participate in every discipline.
    public void ctryTakeAllDis() {
        try {
            String qry = "select DISTINCT c1.countryName from country c1 natural join athletes a1 where not EXISTS\n" +
                    "\t(select discipline_code from discipline EXCEPT\n" +
                    "\t\tselect discipline_code from discipline natural join country c2 natural join athletes a2 \n" +
                    "\t\twhere c1.countryName = c2.countryName )";
            Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(qry);

            while (rs.next()) {
                System.out.println(rs.getString("countryName"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }
}

