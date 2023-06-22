package project;

import java.util.ArrayList;

public class SingleSolution {
	ArrayList<Job> jobs = new ArrayList<Job>();
	double costMatrix [][];
	int solution [];
	int NJ = 0; 
	int NW = 0;

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public void setJobs(ArrayList<Job> jobs) {
		this.jobs = jobs;
	}

	public int[] getSolution() {
		return solution;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
	}
	
	

	public SingleSolution(double[][] costMatrix) { 
		this.costMatrix = costMatrix;
		this.NJ = this.costMatrix[0].length; 
		this.NW = this.costMatrix.length;
		this.solution = new int[NJ]; //nbre de jobs 
	}
	
	public void addJob(Job j) {
		if(!this.jobs.contains(j)) { 
			this.jobs.add(j);
		}
	}
	
	public void showSolution() {
		
		for (int i = 0; i < this.jobs.size(); i++) {
			Job job = this.jobs.get(i);
			System.out.println();
			System.out.print("Job "+(job.ID)+" =====>  Worker "+job.assignedWorker.ID+"  ====> ("+this.costMatrix[job.assignedWorker.ID-1][job.ID-1]+")");
			
			int jobIndex = job.ID-1; 
			int workerIndex = job.assignedWorker.ID-1;
			System.out.println();
			int val = 0; 
			// the last worker has pow index 0 
			for (int j = 0; j < NW; j++) {
				System.out.print(workerIndex==j?"1":"0");
				val = (int) (val + Math.pow(2, NW-j-1)*(workerIndex==j?1:0));
			}
			System.out.println();
			System.out.println("Value ==> "+val);
			this.solution[i] = val;
		}
	}

	
	
	
}
