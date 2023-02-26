public class PCB {

    public String processID;
    public int priority;
    public int arrivalTime;
    public int CPUBurst;
    public int startTime;
    public int terminationTime;
    public int turnAroundTime;
    public int waitingTime;
    public int responseTime;
    public boolean flag;
    public int newArrivalTime;
    public int remainingCPUBurst;

    public PCB(String processID, int arrivalTime, int CPUBurst, int priority, int flag) {

        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.newArrivalTime = arrivalTime;
        this.CPUBurst = CPUBurst;
        this.remainingCPUBurst = CPUBurst;
        this.startTime = 0;
        this.terminationTime = 0;
        this.turnAroundTime = 0;
        this.waitingTime = 0;
        this.responseTime = 0;
        this.flag = false;
    }

    public String toString() {
        return "processID: " + processID + "| priority: " + priority + "| arrivalTime: " + arrivalTime
                + "| CPUBurst: " + CPUBurst + "| startTime: " + startTime + "| terminationTime: " + terminationTime
                + "| turnAroundTime: " + turnAroundTime + "| waitingTime: " + waitingTime + "| responseTime: " + responseTime;
    }
}