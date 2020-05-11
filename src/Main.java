import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Main {

	static ArrayList<Jedinec> Population = new ArrayList<Jedinec>();
	static Jedinec BestSolution = null;
	static Jedinec FoundTreasureBest = null;
	static boolean Map[][];
	static VirtualMachine testS = new VirtualMachine();
	static boolean pokr = true;
	static boolean Question = true;
	static int bestnumFitJed;
	
/*
 * 	Funkcia sluzi na vypis cesty pre jedinca. Cestu vypisujem tak, ze ho znova simulujem, ale uz printujem na konzolu jeho pohyby
 *  Uzivatel si tu dokaze zvolit ci chce pokracovat v simulacii, ak este su nejake generacie, alebo ukonci
 */
	
	static boolean printPath(Jedinec currJed, int mapX, int mapY, int maxTreasure) {
		Scanner sc = new Scanner(System.in);
		testS.Simulate(currJed, Map, mapX, mapY, maxTreasure, true);
		if(Question == true) {
			Question = false;
			System.out.println("\nChcete pokracovat az dokonca?(1/0)");
			int f = sc.nextInt();
			sc.close();
			if(f==0)
				return true;
			else
				return false;
		}else {
			sc.close();
			return false;
		}
	}
	
/*
 * 	Funkcia sluzi na vytvorenie prvej generacie. Bunky sa generuju nahodne od prvej. Uzivatel si dokaze zvolit, kolko buniek
 *  chce vygenerovat. Do buniek vkladam cisla od 0(00000000) az po 255(11111111).
 */
	
	static void CreateFirstGen(int TMapX, int TMapY, int pocB) {
		Random rand = new Random();
		for(int i=0;i<100;i++) {
			Jedinec newSP = new Jedinec(TMapX, TMapY, 0, 0 ,0);
			for(int j=0;j<64;j++) {
				if(j<=pocB)
					newSP.setMemoryPoint(j, (char) rand.nextInt(255));
				else
					newSP.setMemoryPoint(j, (char) 0);
			}
			Population.add(newSP);
		}
	}
	
/*
 * 	Funkcia vytvori nasledovnikov. Nasledovnikov tvorim krizenim z 10 predchadzajucich, a s urcitou sancou aktivujem mutiaciu
 *  Jedinci maju 60% sancu na ziskanie genu z druheho rodica, 20% sancu na ziskanie z druheho + mutacia. A 20% na mutaciu od prveho rodica.
 *  Mutacie su 3 typy. Invertovanie nahodneho bitu (60%), invertovanie celej bunky (20%) a vygenerovanie novej bunky (20%).
 */
	
	static void CreateSucc(int Survivors[], int startX, int startY) {
		Random ran = new Random();
		ArrayList<Jedinec> newPopulation = new ArrayList<Jedinec>();
		for(int i=0;i<10;i++) {
			Jedinec copyJ = new Jedinec(Population.get(Survivors[i]).getPosX(), Population.get(Survivors[i]).getPosY(), 0, 0, 0);
			copyJ.setMemory(Population.get(Survivors[i]).getMemory());
			newPopulation.add(copyJ);
		}
		
		for(int i=0;i<10;i++) {
			for(int j=0;j<10;j++) {
				if(i!=j) {
					Jedinec newJed = new Jedinec(startX, startY, 0, 0, 0);
					newJed.setMemory(Population.get(Survivors[i]).getMemory().clone());
					char ParentMem1[]=Population.get(Survivors[i]).getMemory().clone();
					char ParentMem2[]=Population.get(Survivors[j]).getMemory().clone();
					for(int k=0;k<64;k++) {
						int Chance = ran.nextInt(100);
						if(Chance<=50) {
							newJed.setMemoryPoint(k, ParentMem2[k]);
						}else if(Chance>=90){
							newJed.setMemoryPoint(k, ParentMem2[k]);
							int typeOfMut = ran.nextInt(10);
							if(typeOfMut<7) {				//inverzia nahodneho bitu
								int position = ran.nextInt(8);
								String CurrMemBinMut = String.format("%8s", Integer.toBinaryString(ParentMem2[k])).replace(' ', '0');
								StringBuilder newMem = new StringBuilder(CurrMemBinMut);
								if(newMem.charAt(position)=='1') {
									newMem.setCharAt(position, '0'); 
								}else {
									newMem.setCharAt(position, '1'); 
								}
								int CharFromBin = Integer.parseInt(CurrMemBinMut, 2);
								newJed.setMemoryPoint(k, (char) CharFromBin);
							}if((typeOfMut>=7)&&(typeOfMut<9)) {				//inverzia celej bunky
								String CurrMemBinMut = String.format("%8s", Integer.toBinaryString(ParentMem2[k])).replace(' ', '0');
								CurrMemBinMut = CurrMemBinMut.replace('0', '2').replace('1', '0').replace('2', '1');
								int CharFromBin = Integer.parseInt(CurrMemBinMut, 2);
								newJed.setMemoryPoint(k, (char) CharFromBin);
							}if(typeOfMut>=9) {				//nova bunka
								newJed.setMemoryPoint(k, (char) ran.nextInt(255));
							}
						}else if((Chance<60)&&(Chance>50)) {
							int typeOfMut = ran.nextInt(10);
							if(typeOfMut<7) {				//inverzia nahodneho bitu
								int position = ran.nextInt(8);
								String CurrMemBinMut = String.format("%8s", Integer.toBinaryString(ParentMem1[k])).replace(' ', '0');
								StringBuilder newMem = new StringBuilder(CurrMemBinMut);
								if(newMem.charAt(position)=='1') {
									newMem.setCharAt(position, '0'); 
								}else {
									newMem.setCharAt(position, '1'); 
								}
								int CharFromBin = Integer.parseInt(CurrMemBinMut, 2);
								newJed.setMemoryPoint(k, (char) CharFromBin);
							}if((typeOfMut>=7)&&(typeOfMut<9)) {				//inverzia celej bunky
								String CurrMemBinMut = String.format("%8s", Integer.toBinaryString(ParentMem1[k])).replace(' ', '0');
								CurrMemBinMut = CurrMemBinMut.replace('0', '2').replace('1', '0').replace('2', '1');
								int CharFromBin = Integer.parseInt(CurrMemBinMut, 2);
								newJed.setMemoryPoint(k, (char) CharFromBin);
							}if(typeOfMut>=9) {				//nova bunka
								newJed.setMemoryPoint(k, (char) ran.nextInt(255));
							}
						}
					}
					newPopulation.add(newJed);
					ParentMem1 = null;
					ParentMem2 = null;
				}
			}
		}
		Population.clear();
		for(int i=0;i<100;i++) {
			Population.add(newPopulation.get(i));
		}
		}
	
/*
 * 	Funckia sluzi na ziskanie prezivsich z predchadzajucej generacie. Ziskavam ich pomocou dvoch metod. Metodu si pouzivatel zvoli pri spusteni.
 *  Ziskavam ich ruletov, kde si vzdy zachovam najlepsieho jedinca, aby som nestratil najlepsi genom a zvysok ziska podla svojej fitness
 *  bunky v rulete. Ruleta potom nahodne vyberie 9 jedincov. 
 *  Dalsia moznost je turnaj. Pri turnaji si nevyberam najlepsieho jedinca, pretoze sa vzdy dostane do dalsej generacie (pretoze dokaze kazdeho
 *  porazit). Vzdy vyberam 10 jedincov, a z nich vyberiem najsilnejsieho (vyhral suboj).
 *  Nakoniec si zavolam funkciu CreateSucc, ktorou vytvorim nasledovnikov.
 */
	
	static void DetermineSurvivors(int bestJedinec, int rul, int elit) {
		Random ran = new Random();
		int AllFit = 0, increment = 0;
		int Survivors[] = new int[10];
		if(rul == 1) {
			if(elit == 1) {
			Survivors[increment] = bestJedinec;
			increment++;
			ArrayList<Integer> Roulette = new ArrayList<Integer>();
			for(int i=0;i<100;i++) {
				AllFit += Population.get(i).getFitness();
				for(int j=0;j<Population.get(i).getFitness();j++) {
					Roulette.add(i);
				}
			}
			for(int i=0;i<9;i++) {
				Survivors[increment] = Roulette.get(ran.nextInt(AllFit));
				increment++;
			}
		}else {
			ArrayList<Integer> Roulette = new ArrayList<Integer>();
			for(int i=0;i<100;i++) {
				AllFit += Population.get(i).getFitness();
				for(int j=0;j<Population.get(i).getFitness();j++) {
					Roulette.add(i);
				}
			}
			for(int i=0;i<10;i++) {
				Survivors[increment] = Roulette.get(ran.nextInt(AllFit));
				increment++;
			}
		}
		}if(rul == 0) {
			int position = 0;
			for(int i=0;i<10;i++) {
				int turnBest = position;
				position++;
				for(int j=1;j<10;j++) {
					if(Population.get(position).getFitness()>Population.get(turnBest).getFitness())
						turnBest = position;
					position++;
				}
				Survivors[increment] = turnBest;
				increment++;
			}
		}
		CreateSucc(Survivors, Population.get(0).getPosX(), Population.get(0).getPosY());
	}
	
/*
 * 	Funckia sluzi na zistenie fitness pre jedinca. Fitness pocitam od poctu najdenych pokladov, prejdenych krokov na mape
 *  a ci nahodou vysiel z mapy. Za kazdy najdeny poklad dostane jedinec 100 bodov. Za kazdy krok na mape sa odcita bod (tymto chcem 
 *  prinutit jedincov najst najkratsiu cestu). Za vybocenie z mapy strhnem 25*(celkovy pocet pokladov - najdeny pocet pokladov).
 *  Takto chcem viac potrestat jedincov, ktori nenasli ziaden poklad.
 */
	
	static int setFitness(int NumOfTreasure, int noGen) {
		int bestJedinec = 0, bestJedFit = 0;
		int numOfT =0, numOfStepsMap = 0;
		boolean Out = false;
		for(int i=0;i<100;i++) {
			numOfT = Population.get(i).getTreasure();
			Out = Population.get(i).getKill();
			numOfStepsMap= Population.get(i).getNumOfSteps();
			Population.get(i).setFitness(Population.get(i).getFitness()+(numOfT*100-numOfStepsMap));
			if(Out == true) {
				Population.get(i).setFitness(Population.get(i).getFitness()-25*(NumOfTreasure-numOfT));
			}
			if(Population.get(i).getFitness()<1) {
				Population.get(i).setFitness(1);
			}
			if(Population.get(i).getFitness()>bestJedFit) {
				bestJedFit = Population.get(i).getFitness();
				bestJedinec = i;
			}
			if(FoundTreasureBest != null) {														//ukladam si najlepsieho jedinca, ked nasiel vsetky poklady
				if(Population.get(i).getTreasure() == NumOfTreasure) {
					if(Population.get(i).getFitness() > FoundTreasureBest.getFitness()) {
						Jedinec NewCopy = new Jedinec(Population.get(bestJedinec).getPosX(), Population.get(bestJedinec).getPosY(), 0, 
								0, Population.get(bestJedinec).getFitness());
						NewCopy.setMemory(Population.get(bestJedinec).getMemory());
						NewCopy.setKill(false);
						FoundTreasureBest = NewCopy;
					}
				}
			}
		}
		if(BestSolution == null) {																//ukladam globalne najlepsieho jedinca
			Jedinec NewCopy = new Jedinec(Population.get(bestJedinec).getPosX(), Population.get(bestJedinec).getPosY(), 0, 
					0, Population.get(bestJedinec).getFitness());
			NewCopy.setMemory(Population.get(bestJedinec).getMemory());
			NewCopy.setKill(false);
			BestSolution = NewCopy;
		}else {
			if(BestSolution.getFitness()<Population.get(bestJedinec).getFitness()) {
				Jedinec NewCopy = new Jedinec(Population.get(bestJedinec).getPosX(), Population.get(bestJedinec).getPosY(), 0, 
						0, Population.get(bestJedinec).getFitness());
				NewCopy.setMemory(Population.get(bestJedinec).getMemory());
				NewCopy.setKill(false);
				BestSolution = NewCopy;
			}
		}
		return bestJedinec;
	}
	
/*
 * 	Funkcia sluzi na simulovanie populacie. Pre simulovanie si volam funkciu testS.Simulate(), kde som si vytvoril "virtualny stroj"
 *  Funkcia taktiez kontroluje ci jedinec nasiel vsetky poklady.
 */
	
	static boolean SimulateCurrPopulation(int MapX, int MapY, int NumOfTreasure, int gen) {
		int best = 0;
		for(int i=0;i<100;i++) {
			Jedinec currJed = Population.get(i);
			testS.Simulate(currJed, Map, MapX, MapY, NumOfTreasure, false);
			if(Population.get(best).getFitness()<currJed.getFitness()) {
				best = i;
			}
			if(currJed.getTreasure() == NumOfTreasure) {
				if(pokr == true) {
				System.out.println("NASIEL SA STASTLIVEC");
				System.out.println("Generacia "+gen);
				pokr=printPath(currJed, MapX, MapY, NumOfTreasure);
				}
				if((FoundTreasureBest == null) ){
					if((currJed.getTreasure()==NumOfTreasure)) {
							Jedinec NewCopy = new Jedinec(currJed.getPosX(), currJed.getPosY(), 0, 
									0, currJed.getFitness());
							NewCopy.setMemory(currJed.getMemory());
							NewCopy.setKill(false);
							FoundTreasureBest = NewCopy;
					}
				}else {
					if(FoundTreasureBest.getFitness()<currJed.getFitness()) {
						Jedinec NewCopy = new Jedinec(currJed.getPosX(), currJed.getPosY(), 0, 
								0, currJed.getFitness());
						NewCopy.setMemory(currJed.getMemory());
						NewCopy.setKill(false);
						FoundTreasureBest = NewCopy;
					}
				}
				if(pokr == true) {
					bestnumFitJed = best;
					return pokr;
				}
			}else {
				if(BestSolution == null) {																//ukladam globalne najlepsieho jedinca
					Jedinec NewCopy = new Jedinec(currJed.getPosX(), currJed.getPosY(), 0, 
							0, currJed.getFitness());
					NewCopy.setMemory(currJed.getMemory());
					NewCopy.setKill(false);
					BestSolution = NewCopy;
				}else {
					if(BestSolution.getFitness()<currJed.getFitness()) {
						Jedinec NewCopy = new Jedinec(currJed.getPosX(), currJed.getPosY(), 0, 
								0, currJed.getFitness());
						NewCopy.setMemory(currJed.getMemory());
						NewCopy.setKill(false);
						BestSolution = NewCopy;
					}
				}
			}
		}
		bestnumFitJed = best;
		return false;
	}

/*
 * Funkcia, ktora simuluje generacie. Pocet generacii si pouzivatel zada pri starte. Tato funckia vola potom zvysne funkcie.
 */
	
	static void findTreasure(int MapX, int MapY, int TMapX, int TMapY, int NumOfTreasure, int NumOfGen, int pocB, int rul, int elit) {
		CreateFirstGen(TMapX, TMapY, pocB);
		for(int i=0;i<NumOfGen;i++) {
			if(SimulateCurrPopulation(MapX, MapY, NumOfTreasure,i)) {
				break;
			}
			//bestnumFitJed = setFitness(NumOfTreasure, i);
			DetermineSurvivors(bestnumFitJed, rul, elit);

		}
	}

/*
 * 	V maine sa nacita subor, ktory zada pouzivatel. Subor sa postupne precita a rozdeli. Podla zadanych parametrov sa zisti poloha pokladov,
 *  hladaca, velkost mapy. Uzivatel si potom zvoli pocet buniek, ktore sa inicializuju, pocet generacii a typ selekcie. Nakoniec sa zavola
 *  funkcia findTreasure, ktora zacne simulaciu.
 */
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner s = new Scanner(System.in);
		System.out.println("Zadajte nazov suboru (.txt)");
        System.out.println("Subor musi mat tvar a indexuje sa od 0: '(rozmery('x/y') mapy pozicia hladajuceho('x/y')\npozicie pokladov('x/y'))'");
        String fileName;
        //fileName = s.nextLine();
		while(true) 
		{
			fileName = s.nextLine();
			File myObj = new File(fileName);
			if(myObj.exists()) 
			{
				break;
			}
			System.out.println("Subor neexistuje");
			}
		File myObj = new File(fileName);
		Scanner myReader = new Scanner(myObj);
		String data =  new String();
		int MapX=0, MapY=0;
		int TMapX=0, TMapY=0;
		data = myReader.nextLine();
		String dataParse[] = data.split(" ");
		String MAPS[] = dataParse[0].split("/");
		MapX = Integer.parseInt(MAPS[0]);
		MapY = Integer.parseInt(MAPS[1]);
		String MAPT[] = dataParse[1].split("/");
		TMapX = Integer.parseInt(MAPT[0]);
		TMapY = Integer.parseInt(MAPT[1]);
		Map=new boolean[MapY][MapX];
		for(int i=0;i<MapY;i++) {
		  for(int j=0;j<MapX;j++) {
		        Map[i][j]=false;
		    	}
		    }
		data = myReader.nextLine();
		String MapdataParse[] = data.split(" ");
		    
		for(int i=0;i<MapdataParse.length;i++) {
			 String AKT[] = MapdataParse[i].split("/");
			 Map[Integer.parseInt(AKT[1])][Integer.parseInt(AKT[0])]=true;
		}
		System.out.println("Zadajte pocet buniek, ktore sa maju na zaciatku inicializovat (max 64)");
		int pocB = s.nextInt();
		System.out.println("Kolko chcete generacii?");
		int pocG = s.nextInt();
		System.out.println("Chcete ruletu alebo turnaj(1/0)");
		int RUL = s.nextInt();
		System.out.println("Chcete elitarizmus?(1/0)");
		int elit =  s.nextInt();
		for(int i=0;i<MapY;i++) {
		    for(int j=0;j<MapX;j++) {
		    	if((i==TMapY)&&(j==TMapX)) {
		    		System.out.print("HRAC ");
		    	}else {
		    		if(Map[i][j] == false) {
		    			System.out.print("cesta ");
		    		}else {
		    			System.out.print("poklad ");
		    		}
		    		}
		    	}
		    	System.out.println("");
		    }
		    findTreasure(MapX, MapY, TMapX, TMapY, MapdataParse.length , pocG, pocB, RUL, elit);
		    if((pokr == false) ||((FoundTreasureBest==null))){
		    	if(FoundTreasureBest==null) {
		    		System.out.println("Nenasli sa vsetky poklady");
		    		printPath(BestSolution, MapX, MapY, MapdataParse.length);
		    	}else {
		    		printPath(FoundTreasureBest, MapX, MapY, MapdataParse.length);
		    	}
		    }
		    myReader.close();
		s.close();
		
	}
}

