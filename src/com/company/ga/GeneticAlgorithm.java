package com.company.ga;

import java.util.ArrayList;  
import java.util.Date;
import java.util.List;  
  
    
public abstract class GeneticAlgorithm {  
    private List<Chromosome> population = new ArrayList<Chromosome>();  
    /**��Ⱥ����*/
    private int popSize = 40;
    /**Ⱦɫ����󳤶�*/
    private int geneSize;
    /**����������*/
    private int maxIterNum = 1000; 
    /**�������ĸ���*/
    private double mutationRate = 0.001;
    /**�����첽��*/
    private int maxMutationNum = 3;  
    /**��ǰ�Ŵ����ڼ���*/  
    private int generation = 1;  
      
    private double bestScore;//��õ÷�  
    private double worstScore;//��÷�  
    private double totalScore;//�ܵ÷�  
    private double averageScore;//ƽ���÷�  
      
    private double x; //��¼��ʷ��Ⱥ����õ�Xֵ  
    private double y; //��¼��ʷ��Ⱥ����õ�Yֵ  
    private int geneI;//x y���ڴ���  
    
    private DynamicDataWindow ddWindow;
    private long tp;
    
    public GeneticAlgorithm(int geneSize) {  
        this.geneSize = geneSize;  
    }  
      
    public void caculte() { 
    	
        //1.��ʼ����Ⱥ  
        init(); 
        for(generation = 1; generation < maxIterNum; generation++) {  
        	//2.������Ⱥ��Ӧ��
        	caculteScore(); 
        	System.out.println("3>��֤��ֵ...");
            //4.��Ⱥ�Ŵ�  
            evolve();
          //5.����ͻ��  
            mutation();
            print();  
        }  
    }  
      
    /** 
     * @Description: ������ 
     */  
    private void print() {  
        System.out.println("--------------------------------");  
        System.out.println("the generation is:" + generation);  
        System.out.println("the best y is:" + bestScore);  
        System.out.println("the worst fitness is:" + worstScore);  
        System.out.println("the average fitness is:" + averageScore);  
        System.out.println("the total fitness is:" + totalScore);  
        System.out.println("geneI:" + geneI + "\tx:" + ((x-10000.0)/10000.0) + "\ty:" + (y));
     	
        long millis=System.currentTimeMillis();
        if (millis-tp>300) {
        	tp=millis;
//        	ddWindow.setyMaxMin(y-10);
     		ddWindow.addData(millis, y);
		}
       
		try {
			Thread.sleep(10L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        			
    }  
      
      
    /** 
     * @Description: ��ʼ����Ⱥ 
     */  
    private void init() {  
    	System.out.println("1>���ɳ�ʼ��Ⱥ...");
    	ddWindow.setVisible(true);
    	population = new ArrayList<Chromosome>();
        for (int i = 0; i < popSize; i++) {  
            Chromosome chro = new Chromosome(geneSize);  
            if(chro.getNum()<=30000) {
            	population.add(chro);
            }else {
            	i--;
            }
              
        }  
    }  
      
    /** 
     * @Description:��Ⱥ�����Ŵ� 
     */  
    private void evolve() {  
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();  
        //������һ����Ⱥ  
        while (childPopulation.size() < popSize) {  
            Chromosome parent1 = getParentChromosome();  
            Chromosome parent2 = getParentChromosome();  
            List<Chromosome> children = Chromosome.genetic(parent1, parent2);  
            if (children != null) {  
                for (Chromosome chro : children) {
                	if(chro.getNum()<=30000) {
                		childPopulation.add(chro);
                	}
                     
                }  
            }   
        }  
        System.out.println("4.2>�����Ӵ���Ⱥ...");
        //����Ⱥ�滻����Ⱥ  
        population.clear();  
        population = childPopulation;  
    }  
      
    /** 
     * @return 
     * Email: tyhj_sf@163.com   
     * @Description: ���̶ķ�ѡ������Ŵ���һ����Ⱦɫ�� 
     */  
    private Chromosome getParentChromosome (){  
    	System.out.println("4.1>ɸѡ������Ⱥһ��...");
    	while (true) {
    		double slice = Math.random() * totalScore;  
            double sum = 0;  
            for (Chromosome chro : population) {  
                sum += chro.getScore();  
                System.out.println("���ԣ�sum="+sum+"  chro.getScore()="+chro.getScore());
                if (sum > slice && chro.getScore() >= averageScore) {  
                    return chro;  
                }  
            }
		}
    }  
      
    /** 
     * @Description: ������Ⱥ��Ӧ�� 
     */  
    private void caculteScore() {
    	System.out.println("2>������Ⱥ��Ӧ��...");
    	bestScore=(double)population.get(0).getScore();
    	worstScore=(double)population.get(0).getScore();
        totalScore = 0;  
        for (Chromosome chro : population) {  
            setChromosomeScore(chro);  
            if (chro.getScore() > bestScore) { //������û���ֵ  
                bestScore = chro.getScore();  
                if (y < bestScore) {  
                    x = changeX(chro);  
                    y = bestScore;  
                    geneI = generation;  
                }  
            }  
            if (chro.getScore() < worstScore) { //���������ֵ  
                worstScore = chro.getScore();  
            }  
            totalScore += chro.getScore();  
        }  
        averageScore = totalScore / popSize;  
        //��Ϊ�������⵼�µ�ƽ��ֵ�������ֵ����ƽ��ֵ���ó����ֵ  
        averageScore = averageScore > bestScore ? bestScore : averageScore;  
    }  
      
    /** 
     * ����ͻ�� 
     */  
    private void mutation() {  
    	System.out.println("5>����ͻ��...");
        for (Chromosome chro : population) {  
            if (Math.random() < mutationRate) { //��������ͻ��  
                int mutationNum = (int) (Math.random() * maxMutationNum); 
                Chromosome tempchro=chro;
                boolean flag=true;
                while(flag) {
                	chro.mutation(mutationNum);
                	if(chro.getNum()<=30000) {
                		flag=false;
                	}else {
                		chro=tempchro;
                	}
                }
                  
            }  
        }  
    }  
      
    /** 
     * @param chro 
     * @Description: ���㲢����Ⱦɫ����Ӧ�� 
     */  
    private void setChromosomeScore(Chromosome chro) {  
        if (chro == null) {  
            return;  
        }  
        int x = changeX(chro); 
        //double y = caculateY((x&56)>>3, x&7);//ע����ݾ��������ֵ����ȷ��������ָ�ֵ���˴���Ϊ�������Բ���Ҫ����
        double y = caculateY((x-10000.0)/10000.0);
        chro.setScore(y);  
  
    }  
      
    /** 
     * @param chro 
     * @return 
     * @Description: ��������ת��Ϊ��Ӧ��X 
     */  
    public abstract int changeX(Chromosome chro);  
      
      
    /** 
     * @param x 
     * @return 
     * @Description: ����X����Yֵ Y=F(X) 
     */  
    public abstract double caculateY(int x1, int x2);
    public abstract double caculateY(double x1);
  
    public void setPopulation(List<Chromosome> population) {  
        this.population = population;  
    }  
  
    public void setPopSize(int popSize) {  
        this.popSize = popSize;  
    }  
  
    public void setGeneSize(int geneSize) {  
        this.geneSize = geneSize;  
    }  
  
    public void setMaxIterNum(int maxIterNum) {  
        this.maxIterNum = maxIterNum;  
    }  
  
    public void setMutationRate(double mutationRate) {  
        this.mutationRate = mutationRate;  
    }  
  
    public void setMaxMutationNum(int maxMutationNum) {  
        this.maxMutationNum = maxMutationNum;  
    }  
  
    public double getBestScore() {  
        return bestScore;  
    }  
  
    public double getWorstScore() {  
        return worstScore;  
    }  
  
    public double getTotalScore() {  
        return totalScore;  
    }  
  
    public double getAverageScore() {  
        return averageScore;  
    }  
  
    public double getX() {  
        return x;  
    }  
  
    public double getY() {  
        return y;  
    }  
    
    public DynamicDataWindow getDdWindow() {
		return ddWindow;
	}
    
    public void setDdWindow(DynamicDataWindow ddWindow) {
		this.ddWindow = ddWindow;
	}
}  
