package celeste.comic_community_4_1.miscellaneous;

public class AnalysisData {

    private long[] contribution = new long[4];
    private double[] profit = new double[4];
    private long[] view = new long[4];
    private long[] subscriber = new long[4];

    public long[] getContribution() {
        return contribution;
    }

    public void setContribution(long[] contribution) {
        this.contribution = contribution;
    }

    public double[] getProfit() {
        return profit;
    }

    public void setProfit(double[] profit) {
        this.profit = profit;
    }

    public long[] getView() {
        return view;
    }

    public void setView(long[] view) {
        this.view = view;
    }

    public long[] getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(long[] subscriber) {
        this.subscriber = subscriber;
    }
}
