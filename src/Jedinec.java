
/*
 * Objekt, ktory drzi informacie o jedincovi populacie. Jedinec ma 8 bitove pamatove bunky (64), v ktorych je ulozene jeho spravanie.
 * Taktiez si pamata jeho fitness, pocet najdenych pokladov, pocet prejdenych krokov, zaciatocnu poziciu a informaciu o tom, ci vybehol z mapy
 */

public class Jedinec {
	private char Memory[] = new char[64];
	private int fitness = 0;
	private boolean kill = false;
	private int treasure = 0;
	private int posX;
	private int posY;
	private int numOfSteps;
	private int numOfoperad;
	
	public Jedinec(int StartposX, int StartposY, int numOfSteps, int treasure, int fitness) {
		this.setPosX(StartposX);
		this.setPosY(StartposY);
		this.numOfSteps = numOfSteps;
		this.treasure = treasure;
		this.fitness = fitness;
	}
	
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	public int getFitness() {
			return fitness;
	}

	public char[] getMemory() {
		return Memory;
	}
	
	public void setMemory(char copyM[]) {
		this.Memory = copyM.clone();
	}
	
	public void setMemoryPoint(int Position, char replace) {
		this.Memory[Position] = replace;
	}
	public void setKill(boolean kill) {
		this.kill = kill;
	}
	
	public boolean getKill() {
		return kill;
	}

	public int getTreasure() {
		return treasure;
	}

	public void setTreasure(int treasure) {
		this.treasure = treasure;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getNumOfSteps() {
		return numOfSteps;
	}

	public void setNumOfSteps(int numOfSteps) {
		this.numOfSteps = numOfSteps;
	}

	public int getNumOfoperad() {
		return numOfoperad;
	}

	public void setNumOfoperad(int numOfoperad) {
		this.numOfoperad = numOfoperad;
	}
}
