
/*
 * Tu simulujem jedinca. Vytvoril som funkciu Simulate, ktora dokaze rozoznavat 4 typy prikazov. Ak su prve dva bity 00, tak jedinec inkrementuje
 * hodnotu na adrese ktora je napisana na zvysnych 6 bitoch. Ak je tam 01 tak jedinec dekrementuje, ak je 10 tak jedinec skace a ak je 11 tak vypisuje pohyb.
 * Pri vypise pohybu pocitam pocet jednotiek v bunke. Ak je menej ako 2 tak ide hore, ak 3-4 tak ide dole, ak 5-6 tak vpravo ak viac ako 6 tak vlavo.
 * Stroj taktiez kontroluje poziciu jedinca, a ak vybehne z mapy tak ukonci simulaciu, ak sa dostane na poslednu bunku tak prejde spat na prvu
 * a ak vykona 500 instrukcii tak taktiez ukonci. Ak najde jedinec vsetky poklady tak simulaciu tiez ukoncim.
 */

public class VirtualMachine {
	
	public boolean Simulate(Jedinec jedinec, boolean Map[][], int mapX, int mapY, int maxTreasure, boolean Print) {
		char JedinecMemory[] = jedinec.getMemory().clone();
		boolean MapCop[][] = new boolean[mapY][mapX];
		for(int i=0;i<mapY;i++) {
	    	for(int j=0;j<mapX;j++) {
	    		MapCop[i][j]=Map[i][j];
	    	}
	    }
		int currMoves = 0, memoryPos = 0, currentTreas = 0, movesOnMap = 0;
		char currMemorySlot = JedinecMemory[0];
		int currX = jedinec.getPosX();
		int currY = jedinec.getPosY();
		while(true) {
			if(currMoves == 500) {
				jedinec.setNumOfoperad(currMoves);
				jedinec.setTreasure(currentTreas);
				jedinec.setNumOfSteps(movesOnMap);
				jedinec.setFitness((currentTreas*100-movesOnMap));
				if(jedinec.getFitness()<=0)
					jedinec.setFitness(1);
				break;
			}
			if(memoryPos == 64) {
				memoryPos = 0;
			}
			currMemorySlot = JedinecMemory[memoryPos];
			String CurrMemBin = String.format("%8s", Integer.toBinaryString(currMemorySlot)).replace(' ', '0');
			if(CurrMemBin.substring(0, 2).equals("00")) {						//inkrementujem hodnotu v bunke, ktora je reprezentovana 6 bitmi
				int IncPos = Integer.parseInt(CurrMemBin.substring(2), 2);
				char ReplChar = JedinecMemory[IncPos];
				if((int)ReplChar == 255) {
					ReplChar = 0;
					JedinecMemory[IncPos] = ReplChar;
					memoryPos++;
					if(memoryPos == 64) {
						memoryPos = 0;
					}
					currMemorySlot = JedinecMemory[memoryPos];
				}else {
					ReplChar++;
					JedinecMemory[IncPos] = ReplChar;
					memoryPos++;
					if(memoryPos == 64) {
						memoryPos = 0;
					}
					currMemorySlot = JedinecMemory[memoryPos];
				}
			}if(CurrMemBin.substring(0, 2).equals("01")) {          //dekrementujem hodnotu v bunke, ktora je reprezentovana 6 bitmi
				int IncPos = Integer.parseInt(CurrMemBin.substring(2), 2);
				char ReplChar = JedinecMemory[IncPos];
				if((int)ReplChar == 0) {
					ReplChar = 255;
					JedinecMemory[IncPos] = ReplChar;
					memoryPos++;
					if(memoryPos == 64) {
						memoryPos = 0;
					}
					currMemorySlot = JedinecMemory[memoryPos];
				}else {
					ReplChar--;
					JedinecMemory[IncPos] = ReplChar;
					memoryPos++;
					if(memoryPos == 64) {
						memoryPos = 0;
					}
					currMemorySlot = JedinecMemory[memoryPos];
				}
			}if(CurrMemBin.substring(0, 2).equals("10")) {      //skacem na bunku, ktora je reprezentovana 6 bitmi
				memoryPos = Integer.parseInt(CurrMemBin.substring(2), 2);
				
			}if(CurrMemBin.substring(0, 2).equals("11")) {	    //vypisujem podla hodnoty v bunke, ktora je reprezentovana 6 bitmi
				
				int numOfOne = 0;
				String subSTR = CurrMemBin.substring(2);
				char MoV = JedinecMemory[Integer.parseInt(subSTR,2)];
				String MemBin = String.format("%8s", Integer.toBinaryString(MoV)).replace(' ', '0');
				for(int i=0; i<8; i++) {
					if(MemBin.charAt(i) == '1') {
						numOfOne++;
					}
				}
				
				if((numOfOne > 2)&&(numOfOne <= 4)){                    //ide dole
					if(Print==true) {
						System.out.print("D");
					}
					if(currY +1 >= mapY) {
						jedinec.setNumOfoperad(currMoves);
						jedinec.setKill(true);
						jedinec.setTreasure(currentTreas);
						jedinec.setNumOfSteps(movesOnMap);
						jedinec.setFitness((currentTreas*100-movesOnMap));
						jedinec.setFitness(jedinec.getFitness()-25*(maxTreasure-currentTreas));
						if(jedinec.getFitness()<=0)
							jedinec.setFitness(1);
						break;
					}else {
						movesOnMap++;
						currY++;
						if(MapCop[currY][currX]==true) {
							if(Print==true) {
								System.out.print("X");
							}
							MapCop[currY][currX]=false;
							currentTreas++;
							if(currentTreas == maxTreasure) {
								jedinec.setNumOfoperad(currMoves);
								jedinec.setTreasure(currentTreas);
								jedinec.setNumOfSteps(movesOnMap);
								jedinec.setFitness((currentTreas*100-movesOnMap));
								if(jedinec.getFitness()<=0)
									jedinec.setFitness(1);
								return true;
							}
						}
					}
				}
				if(numOfOne <= 2)  {  								//ide hore
					if(Print==true) {
						System.out.print("H");
					}
					if(currY-1 < 0) {
						jedinec.setNumOfoperad(currMoves);
						jedinec.setKill(true);
						jedinec.setTreasure(currentTreas);
						jedinec.setNumOfSteps(movesOnMap);
						jedinec.setFitness((currentTreas*100-movesOnMap));
						jedinec.setFitness(jedinec.getFitness()-25*(maxTreasure-currentTreas));
						if(jedinec.getFitness()<=0)
							jedinec.setFitness(1);
						break;
					}else {
						movesOnMap++;
						currY--;
						if(MapCop[currY][currX]==true) {
							if(Print==true) {
								System.out.print("X");
							}
							MapCop[currY][currX]=false;
							currentTreas++;
							if(currentTreas == maxTreasure) {
								jedinec.setNumOfoperad(currMoves);
								jedinec.setTreasure(currentTreas);
								jedinec.setNumOfSteps(movesOnMap);
								jedinec.setFitness((currentTreas*100-movesOnMap));
								if(jedinec.getFitness()<=0)
									jedinec.setFitness(1);
								return true;
							}
						}
					}
				}
				if((numOfOne >4)&&(numOfOne <=6)) {   //ide vpravo
					if(Print==true) {
						System.out.print("P");
					}
					if(currX+1 >= mapX) {
						jedinec.setNumOfoperad(currMoves);
						jedinec.setKill(true);
						jedinec.setTreasure(currentTreas);
						jedinec.setNumOfSteps(movesOnMap);
						jedinec.setFitness((currentTreas*100-movesOnMap));
						jedinec.setFitness(jedinec.getFitness()-25*(maxTreasure-currentTreas));
						if(jedinec.getFitness()<=0)
							jedinec.setFitness(1);
						break;
					}else {
						movesOnMap++;
						currX++;
						if(MapCop[currY][currX]==true) {
							if(Print==true) {
								System.out.print("X");
							}
							MapCop[currY][currX]=false;
							currentTreas++;
							if(currentTreas == maxTreasure) {
								jedinec.setNumOfoperad(currMoves);
								jedinec.setTreasure(currentTreas);
								jedinec.setNumOfSteps(movesOnMap);
								jedinec.setFitness((currentTreas*100-movesOnMap));
								if(jedinec.getFitness()<=0)
									jedinec.setFitness(1);
								return true;
							}
						}
					}
				}
				if(numOfOne >6) {					//ide vlavo
					if(Print==true) {
						System.out.print("L");
					}
					if(currX - 1 < 0) {
						jedinec.setNumOfoperad(currMoves);
						jedinec.setKill(true);
						jedinec.setTreasure(currentTreas);
						jedinec.setNumOfSteps(movesOnMap);
						jedinec.setFitness((currentTreas*100-movesOnMap));
						jedinec.setFitness(jedinec.getFitness()-25*(maxTreasure-currentTreas));
						if(jedinec.getFitness()<=0)
							jedinec.setFitness(1);
						break;
					}else {
						movesOnMap++;
						currX--;
						if(MapCop[currY][currX]==true) {
							if(Print==true) {
								System.out.print("X");
							}
							MapCop[currY][currX]=false;
							currentTreas++;
							if(currentTreas == maxTreasure) {
								jedinec.setNumOfoperad(currMoves);
								jedinec.setTreasure(currentTreas);
								jedinec.setNumOfSteps(movesOnMap);
								jedinec.setFitness((currentTreas*100-movesOnMap));
								if(jedinec.getFitness()<=0)
									jedinec.setFitness(1);
								return true;
							}
						}
					}
				}
				memoryPos++;
				if(memoryPos == 64) {
					memoryPos = 0;
				}
			}
			currMoves++;
		}
		jedinec.setNumOfoperad(currMoves);
		jedinec.setTreasure(currentTreas);
		//jedinec.setFitness((currentTreas*100-movesOnMap));
		//if(jedinec.getFitness()<=0)
		//	jedinec.setFitness(1);
		return false;
	}
}
