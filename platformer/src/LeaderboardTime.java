public class LeaderboardTime implements Comparable {

    private String name;
    private double time;

    private String data;

    public LeaderboardTime(double time, String name, String data) {
        this.time = time;
        this.name = name;
        this.data = data;
    }



    public String getName() {
        return name;
    }

    public String getData() {
        return data.replace(":", ";");
    }

    public double getTime() {
        return time;
    }


    public String toString() {
        return name + " - " + Main.formatTime(time) + " " + getData();
    }


    @Override
    public int compareTo(Object o) {
        return (int)(this.time*1000-((LeaderboardTime)o).getTime()*1000);
    }




    public static LeaderboardTime constructTime(String time) {
        return new LeaderboardTime(Double.parseDouble(time.split(":")[0]), time.split(":")[1], time.split(":")[2]);
    }
}
