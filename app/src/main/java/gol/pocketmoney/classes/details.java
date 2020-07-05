package gol.pocketmoney.classes;

public class details {
    private int calorie,credit;
    private Float distance,time,speed;

    public details() { }

    public details(int calorie, int credit, Float distance, Float time, Float speed) {
        this.calorie = calorie;
        this.credit = credit;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }
}
