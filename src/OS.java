import java.util.*;
import java.io.*;

public class OS {

    int choice, NoProcesses, priority, Qcount = 0, Q1count = 0, Q2count = 0;
    int sysTime, finishedProcess, arrivalTime, CPUbusrt, selectedProcess = 0;
    boolean preemptionFlag = false;
    String schedulingOrder = "";
    PCB Q[], newQ[], Q1[], Q2[];
    Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        OS obj = new OS();
        obj.menu();
    }//End main

    public void menu() {
        System.out.println("Enter the number of processes: ");
        NoProcesses = in.nextInt();

        PCB Q[] = new PCB[NoProcesses], Q1[] = null, Q2[] = null;
        PCB newQ[] = new PCB[NoProcesses];//Order of terminated processes

        do {
            System.out.println("\n Choose from the following menu: ");
            System.out.println("1. Enter process' information: ");
            System.out.println("2. Report detaild information about each process.");
            System.out.println("3. Report the average turnaround time, waiting time, and response.");
            System.out.println("4. Exit the program");
            choice = in.nextInt();

            switch (choice) {
                case 1:
                    try {
                        if (Qcount == 0) {
                            //Fill processes information
                            for (int i = 1; i <= NoProcesses; i++) {
                                System.out.println("Process #" + i + " information: ");

                                System.out.println("Enter the arrival time: ");
                                arrivalTime = in.nextInt();

                                System.out.println("Enter CPU burst: ");
                                CPUbusrt = in.nextInt();

                                System.out.println("Enter the priority(1 or 2): ");
                                priority = in.nextInt();

                                if (priority != 1 && priority != 2) {
                                    do {
                                        System.out.println("Invalid priority, reenter the priority again(1 or 2): ");
                                        priority = in.nextInt();
                                    } while (priority != 1 && priority != 2);
                                }
                                Q[Qcount++] = new PCB("P" + i, arrivalTime, CPUbusrt, priority, 0);
                            }

                            //Count # of processes for each queue
                            for (int i = 0; i < NoProcesses; i++) {
                                if (Q[i].priority == 1) {
                                    Q1count++;
                                } else {
                                    Q2count++;
                                }
                            }
                            Q1 = new PCB[Q1count];
                            Q2 = new PCB[Q2count];

                            //Split processes among queues based on priority
                            Q1count = Q2count = 0;
                            for (int i = 0; i < NoProcesses; i++) {
                                if (Q[i].priority == 1) {
                                    Q1[Q1count++] = Q[i];
                                } else {
                                    Q2[Q2count++] = Q[i];
                                }
                            }
                            bubbleSort(Q2);
                            //Start CPU Scheduling Algorithm 
                            Q1count = Q2count = 0;
                            if (Q1.length != 0) {
                                SJF(Q1, Q2, newQ);
                            } else if (Q2.length != 0) {
                                FCFS(Q1, Q2, newQ);
                            }

                        } else {
                            System.out.println("You already entered processes information !");
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println(ex.getMessage());

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case 2:
                    try {
                        //Check if there are no processes yet
                        if (Qcount == 0) {
                            System.out.println("There are no processes yet");
                            break;
                        }

                        //Open Report#1 file
                        File outFile = new File("Report1.txt");
                        FileOutputStream sf = new FileOutputStream(outFile);
                        PrintWriter pf = new PrintWriter(sf);

                        System.out.println("Scheduling Order: " + schedulingOrder);
                        pf.println("Scheduling Order: " + schedulingOrder);
                        System.out.println();
                        pf.println();

                        //Print Q1 and Q2 information
                       
                        if (Q1.length != 0) {
                            System.out.println("Q1 detaild information: ");
                            pf.println("Q1 detaild information: ");
                            for (int i = 0; i < Q1.length; i++) {
                                System.out.println(Q1[i].toString());
                                pf.println(Q1[i].toString());
                            }
                        }
                        System.out.println();
                        
                        if (Q2.length != 0) {
                            System.out.println("Q2 detaild information: ");
                            pf.println("Q2 detaild information: ");
                            for (int i = 0; i < Q2.length; i++) {
                                System.out.println(Q2[i].toString());
                                pf.println(Q2[i].toString());
                            }

                        }
                        System.out.println();

                        //Close Report#1 file
                        pf.close();
                        System.out.println("The output been printed in Report1.txt file ;)");

                    } catch (FileNotFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;

                case 3:
                    //Calculate and print average
                    Average(newQ);
                    break;

                case 4:
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice!");

            }
        } while (choice != 4);

    }

    public void SJF(PCB[] Q1, PCB[] Q2, PCB[] newQ) {
        if (finishedProcess != NoProcesses) {
            while (Q1count <= Q1.length) {
                if (finishedProcess == NoProcesses) {
                    return;
                }

                //spFlag: There is process arrived in Q1 and could be selected for scheduling
                //min: To ensure that the condition is met for the first time
                boolean spFlag = false;
                int min = 10000;

                //Choose the SJ from the arrived processes 
                for (int i = 0; i < Q1.length; i++) {
                    if ((Q1[i].arrivalTime <= sysTime && Q1[i].flag == false) && (Q1[i].CPUBurst < min)) {
                        spFlag = true;
                        min = Q1[i].CPUBurst;
                        selectedProcess = i;
                    }
                }

                //Print process ID and calculate the scheduling criteria of the process that is running in the CPU
                if (spFlag) {
                    schedulingOrder += " | " + Q1[selectedProcess].processID;

                    Q1[selectedProcess].startTime = sysTime;
                    Q1[selectedProcess].terminationTime = (sysTime + Q1[selectedProcess].CPUBurst);
                    Q1[selectedProcess].turnAroundTime = (Q1[selectedProcess].terminationTime - Q1[selectedProcess].arrivalTime);
                    Q1[selectedProcess].waitingTime = (Q1[selectedProcess].turnAroundTime - Q1[selectedProcess].CPUBurst);
                    Q1[selectedProcess].responseTime = Q1[selectedProcess].startTime - Q1[selectedProcess].arrivalTime;
                    sysTime += Q1[selectedProcess].CPUBurst;

                    //Process terminate from CPU
                    Q1[selectedProcess].flag = true;
                    Q1count++;//One process has been terminated
                    newQ[finishedProcess++] = Q1[selectedProcess];
                } else {
                    //If Q2 have processes and there is no process has been preempted at this time
                    if (Q2count != Q2.length && preemptionFlag == false) {
                        FCFS(Q1, Q2, newQ);
                    } //All processes in Q1 have been terminated
                    else if (Q1count == Q1.length) {
                        return;
                    } //No processes arrived in Q1 at this time, and there is process in Q2 was preempted
                    else if (Q2count != Q2.length) {
                        for (int i = 0; i < Q2.length; i++) {
                            if (Q2[i].arrivalTime <= sysTime && preemptionFlag == true) {
                                return;
                            }
                        }
                    } //CPU is idle at this time
                    else {
                        sysTime++;
                    }
                }
            }//End outer while
        }//End outer if
    }//End SJF

    public void FCFS(PCB[] Q1, PCB[] Q2, PCB[] newQ) {
        if (finishedProcess != NoProcesses) {
            int FCFSCount = 0;
            while (FCFSCount <= Q2.length) {
                if (finishedProcess == NoProcesses) {
                    return;
                }

                //spFlag: There is process arrived in Q1 and could be selected for scheduling
                boolean spFlag = false;

                //Choose the FC process in Q2
                for (int i = 0; i < Q2.length; i++) {
                    if (Q2[i].flag == false && Q2[i].newArrivalTime <= sysTime) {
                        if (Q2[i].newArrivalTime == Q2[i].arrivalTime) { //New process came before interrupted process
                            spFlag = true;
                            selectedProcess = i;
                            break;
                        }
                        spFlag = true;
                        selectedProcess = i;
                        //No new arrived process at the same time of interruption, the interrupted process completes execution
                        if ((i + 1) < Q2.length && Q2[i].newArrivalTime != Q2[i + 1].newArrivalTime) {
                            break;
                        }
                    }
                }

                //Print process ID 
                if (spFlag) {
                    schedulingOrder += " | " + Q2[selectedProcess].processID;
                    Q2[selectedProcess].flag = true;//Process start execution
                    //Calculate the scheduling criteria of the process that is start running in the CPU for the first time
                    if (Q2[selectedProcess].newArrivalTime == Q2[selectedProcess].arrivalTime) { 
                        Q2[selectedProcess].startTime = sysTime;
                        Q2[selectedProcess].responseTime = Q2[selectedProcess].startTime - Q2[selectedProcess].arrivalTime;
                    }
                    sysTime++;

                    //Save process info in case of preemption
                    Q2[selectedProcess].remainingCPUBurst--;
                    int currentSysTime = sysTime;
                    int currentSelectedProcess = selectedProcess;

                    while (Q2[currentSelectedProcess].remainingCPUBurst != 0) {
                        //Check if there is arrived process in Q1
                        if (Q1count != Q1.length && currentSysTime == sysTime) {
                            preemptionFlag = true;
                            SJF(Q1, Q2, newQ);

                            //No arrived process in Q1
                            if (currentSysTime == sysTime) {
                                sysTime++;
                                Q2[currentSelectedProcess].remainingCPUBurst--;
                                currentSysTime = sysTime;
                            }

                            FCFSCount = 0;
                            preemptionFlag = false;

                        } else {
                            if (currentSysTime != sysTime) {//Update preempted process info 
                                Q2[currentSelectedProcess].newArrivalTime = currentSysTime;
                                Q2[currentSelectedProcess].flag = false;
                                bubbleSort(Q2);
                                break;

                            }
                            //When Q1 finished execution and system time did not change
                            sysTime++;
                            Q2[currentSelectedProcess].remainingCPUBurst--;
                            currentSysTime = sysTime;
                        }
                    }
                    if (Q2[currentSelectedProcess].remainingCPUBurst != 0) { //In case of interruption find FC process
                        continue;
                    }
                        FCFSCount++;
                        Q2count++;//One process has been terminated
                        newQ[finishedProcess++] = Q2[currentSelectedProcess];
                        //Calculate the scheduling criteria of the terminated process 
                        Q2[currentSelectedProcess].terminationTime = sysTime;
                        Q2[currentSelectedProcess].turnAroundTime = Q2[currentSelectedProcess].terminationTime - Q2[currentSelectedProcess].arrivalTime;
                        Q2[currentSelectedProcess].waitingTime = Q2[currentSelectedProcess].turnAroundTime - Q2[currentSelectedProcess].CPUBurst;
                    
                    if (finishedProcess == NoProcesses) {
                        FCFSCount = Q2.length;
                        break;
                    }

                   /* Check if there is arrived process in Q1
                    if (finishedProcess != NoProcesses && Q1count != Q1.length) {
                        SJF(Q1, Q2, newQ);
                        FCFSCount = 0;
                    }*/
                } else { //No process in Q1 at this time
                    sysTime++;
                    if (Q1count != Q1.length) {
                        SJF(Q1, Q2, newQ);
                        FCFSCount = 0;
                    }
                }
            }//End outer while
        }//End outer if
    }//End FCFS

    public void Average(PCB[] newQ) {
        double sumTurnaroundTime = 0;
        double sumWaitingTime = 0;
        double sumResponse = 0;

        System.out.println();
        try {
            //Open Report#2 file
            File outFile = new File("Report2.txt");
            FileOutputStream sf = new FileOutputStream(outFile);
            PrintWriter pf = new PrintWriter(sf);

            for (int i = 0; i < NoProcesses; i++) {
                sumTurnaroundTime += newQ[i].turnAroundTime;
                sumWaitingTime += newQ[i].waitingTime;
                sumResponse += newQ[i].responseTime;
            }
            System.out.println("\nAverage turnaround time = " + sumTurnaroundTime / NoProcesses);
            pf.println("\nAverage turnaround time = " + sumTurnaroundTime / NoProcesses);
            System.out.println("Average waiting time = " + sumWaitingTime / NoProcesses);
            pf.println("Average waiting time = " + sumWaitingTime / NoProcesses);
            System.out.println("Average response time = " + sumResponse / NoProcesses);
            pf.println("Average response time = " + sumResponse / NoProcesses);

            //Close Report#2 file
            pf.close();
            System.out.println();
            System.out.println("The output been printed in Report2.txt file ;)");

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }//End Average

    void bubbleSort(PCB[] Q2) {
        int n = Q2.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (Q2[j].newArrivalTime > Q2[j + 1].newArrivalTime) {
                    PCB temp = Q2[j];
                    Q2[j] = Q2[j + 1];
                    Q2[j + 1] = temp;
                }
            }//End innert for
        }//End outer for
    }//End bubbleSort
}//End class