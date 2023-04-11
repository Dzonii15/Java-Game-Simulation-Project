package nikola.makric;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import nikola.makric.cards.*;
import nikola.makric.exceptions.InvalidDimensionException;
import nikola.makric.exceptions.InvalidNumberOfPlayersException;
import nikola.makric.exceptions.NotUniqueNameException;
import nikola.makric.interfaces.CanFloat;

import javax.swing.*;

import nikola.makric.GUI.FileListFrame;
import nikola.makric.GUI.MainFrame;
import nikola.makric.boardobjects.Player;
import nikola.makric.boardobjects.boardCell;
import nikola.makric.boardobjects.figures.ExtraFastFigure;
import nikola.makric.boardobjects.figures.FigureColour;
import nikola.makric.boardobjects.figures.FloatingFigure;
import nikola.makric.boardobjects.figures.GameFigure;
import nikola.makric.boardobjects.figures.RegularFigure;
import nikola.makric.boardobjects.spawns.Ghost;

public class Simulation {
    public static int matrixDimensions;
    public static int numberOfPlayers;
    public static boardCell[][] fieldMatrix;
    public static Player[] players;
    public static ArrayList<Integer> orderOfPlayers;
    public static ArrayList<Integer> pathOfTheGame = new ArrayList<>();
    public static MainFrame referenceOnGUI;
    public static boolean simulationChecker = true;
    public static ArrayList<GameFigure> allOfTheFigures = new ArrayList<>();
    public static Object objectToLock ="";
    public static long startTime = new Date().getTime();
    public static Logger logger;
    private static int numberOfHolesToGenerate;
    private static List<String> specification;
    public static void main(String[] args) {
    	logger = Logger.getGlobal();
    	try {
			FileHandler fh =  new FileHandler("MyLogFile.log",true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
        initiateSimulation();
        startSimulation();
    	writeToFile();
    	referenceOnGUI.setPotez("Simulacija je zavrsila");
    	logger.finest("Simulation ended without interruption");
        }
    
    //funkcija zapocinje simulaciju
    public static void startSimulation() {
    	//promjenljiva prati trenutne figurice na matrici
        ArrayList<GameFigure> currentFiguresOnMat = new ArrayList<>();
        for (int i = 0; i < players.length; i++)
            currentFiguresOnMat.add(players[i].getNextFigureToPlay());
        // Generisanje karti
        Queue<Card> karte = new LinkedList<Card>();
        int CardValue = 1;
        //40 regularnih
        for (int i = 1; i <= 40; i++) {
            if (i % 10 == 0 && i < 40)
                CardValue++;
            karte.add(new RegularCard(CardValue));
        }
        //12 specijalnih
		for(int i =0;i<12;i++)
		{
			karte.add(new SpecialCard());
		}
		//vraca nasumicnu permutaciju
        Collections.shuffle((LinkedList) karte);
        //pokrecemo nit duha
        new Ghost().start();
        //pokrecemo nit za racunanje vremena
        new TimeCounterThread().start();
        //Promjenljiva koja ce mi zaustaviti simulaciju ukoliko nema vise figurica za igru
        while (simulationChecker) {
            //brojac koji govori na kojoj sam figuri trenutno
            int figureCounter = 0;
            //za svaku figuru koja se nalazi na polju
            for (GameFigure figura : currentFiguresOnMat) {
            	//za pauzu
            	if(MainFrame.paused)
            	{
            		synchronized(objectToLock)
            		{
            			try {
            			objectToLock.wait();
            			}catch(Exception e)
            			{
            				logger.log(Level.WARNING, null, e);
            			}
            		}
            	}
            	
            	//provjeravamo da li je na figura null odnosno da nije slucajno korisnik ostao bez figura
                if (figura != null) {
                    //izvlacim sledecu kartu koja ce mi reci koliko ce se figurica mjesta pomaci
                    var sledecaKarta = karte.remove();
                    //kako smo izvukli kartu vracamo je nazad u red
                    karte.add(sledecaKarta);
                    //ukoliko je rijec o regularnoj karti
                    if (sledecaKarta instanceof RegularCard) {
                    	//uzimam broj koji se nalazi na karti
                        int brojNaKarti = ((RegularCard) sledecaKarta).getNumber();
                        //postavlja sliku karte na gui
                           referenceOnGUI.postaviIzvucenuSliku("/Karta-" + brojNaKarti + ".png");
                           //postavljamo opis karte na ekran
                           referenceOnGUI.setPotez("Igrac je izvukao regularnu kartu sa brojem:"+brojNaKarti);
                           try
                           {
                        	   Thread.sleep(1000);
                           }catch(Exception e) {logger.log(Level.WARNING, null, e);}
                           //Racunamo broj poteza koliko ce se pomjeriti na osnovu tipa figurice, ako je u pitanju EFF
                           //figura onda ce se duplirati broj mjesta
                           int positionsToMove = figura.calculateMoves(brojNaKarti);
                           //Konzumiramo ukoliko ima bonusa od dijamanata
                           positionsToMove += figura.consumeDiamonds();
                           //gledamo tip figure
                           String figureType= figura.getType();
                           //uzimamo trenutnu poziciju figura na matrici preko putanje
                            int trenutnaPozicijaFigure = pathOfTheGame.get(figura.getPozicija());
                            //ako je tek stala na "plocu"
                            if(figura.getPozicija()==0)
                            {
                            	//uzimamo vrijeme pocetka njene simulacije
                            	figura.setStartTime(new Date().getTime());
                            	referenceOnGUI.postaviPozicijuNaMatricu(0, trenutnaPozicijaFigure, figura.getColour(),figureType);
                            	//opisujemo potez da
                            	referenceOnGUI.setPotez("Figurica "+figura.getName()+" je zapocela kretanje!");
                            	//biljezimo ovo polje u njen predjeni put
                            	figura.addToPath(pathOfTheGame.get(0));
                            	try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                	logger.log(Level.WARNING, null, e);
                                }
                            	
                            }
                            //krecemo se iterativno polje po polje
                            for (int i = 0; i < positionsToMove; ) {
                            	//radi pauze
                            	if(MainFrame.paused)
                            	{
                            		synchronized(objectToLock)
                            		{
                            			try {
                            			objectToLock.wait();
                            			}catch(Exception e)
                            			{
                            				logger.log(Level.WARNING, null, e);
                            			}
                            		}
                            	}
                            	//gledamo koji mi je index sledeceg polja u nizu putanje
                                int indexSledecegPolja = figura.getPozicija() + 1;
                                //ujedno i dohvatam polje koje se nalazi na tom indeksu
                                int sledecePolje = pathOfTheGame.get(indexSledecegPolja);
                                //dodajemo to polje u njen niz predjenih polja
                                figura.addToPath(sledecePolje);
                                //moram da uzmem koordinate polja gdje figura treba da predje
                                String[] koordinateTekst = getCoordinatesBasedOnNumebr(sledecePolje - 1).split("-");
                                //takodje i njene stare koordinate gdje da je uklonim
                                String[] prethodneKoordinateTekst = getCoordinatesBasedOnNumebr(trenutnaPozicijaFigure - 1).split("-");
                                int[] koordinate = new int[2];
                                int[] bivseKoordinate = new int[2];
                                for (int k = 0; k < koordinateTekst.length; k++) {
                                    koordinate[k] = Integer.parseInt(koordinateTekst[k]);
                                    bivseKoordinate[k] = Integer.parseInt(prethodneKoordinateTekst[k]);
                                }
                                //uzimamo da nam je taj index sledeca pseudopozicija te figure
                                figura.setPozicija(indexSledecegPolja);
                                //provjeravamo da li na tom polju nema slucajno neka druga figura
                                synchronized(referenceOnGUI) {
                                synchronized(fieldMatrix) {
                                // uzimamo mjesto u matrici gdje cemo postaviti figuru
                                var promjenljiva = fieldMatrix[koordinate[0]][koordinate[1]];
                                if (!promjenljiva.hasItFigure()) {
                                	//postavljamo figuru na novu poziciju
                                    promjenljiva.setFigure(figura);
                                    //uklanjamo sa stare
                                    fieldMatrix[bivseKoordinate[0]][bivseKoordinate[1]].setFigure(null); 
                                    //to crtamo na gui-u
                                    referenceOnGUI.postaviPozicijuNaMatricu(trenutnaPozicijaFigure, sledecePolje,
                                            figura.getColour(),figureType);
                                    referenceOnGUI.setPotez("Figurica "+figura.getName()+" je presla sa pozicije "+trenutnaPozicijaFigure+" na poziciju "
                                    		+sledecePolje);
                                    //provjeravamo da trenutna lokacija ima dijamante
                                    if (promjenljiva.hasItDiamond()) {
                                    	//ako ima uklanjamo ga sa mape
                                        promjenljiva.removeDiamond();
                                        //dodajemo to figurici
                                        figura.addDiamond();
                                        //to prikazujemo na gui-u
                                        referenceOnGUI.removeDiamond(sledecePolje);
                                    }
                                    i++;
                                    try {
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                    	logger.log(Level.WARNING, null, e);
                                    }
                                    trenutnaPozicijaFigure = sledecePolje;
                                    if (indexSledecegPolja == pathOfTheGame.size() - 1) {
                                       //figurica je dosla do kraja mape
                                    	//biljezimo njeno vrijeme silaska sa ploce odnosno trajanje kretanja
                                    	figura.setEndTime(new Date().getTime());
                                    	//upisujemo na gui da je figurica zavrsila kretanje
                                    	referenceOnGUI.setPotez("Figurica "+figura.getName()+" je dosla do kraja ");
                                    	//gledamo da li je preostalo figurica za tog igraca
                                        if (players[figureCounter].removeFigure()) {
                                        	//ako jeste dohvatamo sledecu figuricu
                                            currentFiguresOnMat.set(figureCounter,
                                                    players[figureCounter].getNextFigureToPlay());
                                        } else {
                                        	//ako ne taj indeks u nizu trenutnih figurica na ploci postavljamo na null
                                            currentFiguresOnMat.set(figureCounter, null);
                                        }
                                        promjenljiva.setFigure(null);
                                        referenceOnGUI.postaviBojuPolja(trenutnaPozicijaFigure, Color.PINK);
                                        referenceOnGUI.postaviBrojNaLabelu(trenutnaPozicijaFigure);
                                        break;
                                    }
                                   }  }}
                    }
                }else
                {
                	//izvukli smo specijalnu kartu
                	//za pauziranje
                			if(MainFrame.paused)
                        	{
                        		synchronized(objectToLock)
                        		{
                        			try {
                        			objectToLock.wait();
                        			}catch(Exception e)
                        			{
                        				logger.log(Level.WARNING, null, e);
                        			}
                        		}
                        	}
                			//generisemo slucajne pozicije na rupama
                        	ArrayList<Integer> pozicijeNaRupama = Simulation.generateNRandomNumbers(numberOfHolesToGenerate, pathOfTheGame.size()-1);
                			referenceOnGUI.postaviIzvucenuSliku("/Karta-Specijalna.png");
                			referenceOnGUI.setPotez("Igrac je izvukao specijalnu kartu i generisace se "+numberOfHolesToGenerate+" rupa");
                            try
                            {
                         	   Thread.sleep(1000);
                            }catch(Exception e) {logger.log(Level.WARNING, null, e);}
                			synchronized(referenceOnGUI) {
                				synchronized(fieldMatrix) {
                			for(Integer nasumicniBroj : pozicijeNaRupama) {  
                				if(MainFrame.paused)
                            	{
                            		synchronized(objectToLock)
                            		{
                            			try {
                            			objectToLock.wait();
                            			}catch(Exception e)
                            			{
                            				logger.log(Level.WARNING, null, e);
                            			}
                            		}
                            	}
                				//uzimamo poziciju na tom nasumicnom broju
                				int pozicijaNaMapi = pathOfTheGame.get(nasumicniBroj);
                				//uzimamo koordinate na osnovu broja na putanji
                				String koordinateTekst[] = Simulation.getCoordinatesBasedOnNumebr(pozicijaNaMapi-1).split("-");
                				int koordinate[] = new int[] {Integer.parseInt(koordinateTekst[0]),Integer.parseInt(koordinateTekst[1])};
                				referenceOnGUI.drawHole(pozicijaNaMapi);
                				var polje = fieldMatrix[koordinate[0]][koordinate[1]];
                				//ako polje sadrzi figuricu i ta figurica nije instance CanFloat odnosno nije lebdeca figurica
                				if(polje.hasItFigure() && !(polje.Figure instanceof CanFloat))
                				{
                					//opisujemo potez da se postavlja rupa
                					referenceOnGUI.setPotez("Rupa se postavila na poziciju "+pozicijaNaMapi+" i pojela je figuricu!");
                					//trazimo o kojoj je figurici rijec
                					int index = 0;
                					for(int h=0;h<currentFiguresOnMat.size();h++)
                					{
                						if(polje.Figure.getName().equals(currentFiguresOnMat.get(h).getName()))
                						{
                							index = h;
                							break;
                						}
                					}
                					//postavljamo krajnje vrijeme i izracunavamo duzinu kretanja figurice
                					figura.setEndTime(new Date().getTime());
                						if (players[index].removeFigure()) {
                                            currentFiguresOnMat.set(index,
                                                    players[index].getNextFigureToPlay());
                                        } else {
                                            currentFiguresOnMat.set(index, null);
                                        }
                						polje.setFigure(null);
                						 referenceOnGUI.postaviBrojNaLabelu(pozicijaNaMapi);
                						referenceOnGUI.postaviBojuPolja(pozicijaNaMapi, Color.PINK);
                					
                					
                				}else {referenceOnGUI.setPotez("Rupa se postavila na poziciju "+pozicijaNaMapi+" i nije pojela  figuricu!");}		
                				try {
                					Thread.sleep(1000);
                				}catch(Exception e)
                				{
                					logger.log(Level.WARNING, null, e);
                				}
                		      }
                			//uklanjanje rupa
                			for(Integer pozicija : pozicijeNaRupama)
                			{
                				int pozicijaNaMapi = pathOfTheGame.get(pozicija);
                				referenceOnGUI.removeHole(pozicijaNaMapi);		
                			}
                			
                }}
                			
                	
                }
            }
                figureCounter++;
            }
            figureCounter = 0;
            // za prekid simulacije
            boolean hasAnyFiguresLeft = false;
            for (GameFigure figura : currentFiguresOnMat) {
                if (figura != null) {
                    hasAnyFiguresLeft = true;
                    break;
                }
            }
            if (!hasAnyFiguresLeft)
                simulationChecker = false;
        }
    }
    //funkcija sluzi da postavi pocetne parametre simulacije
    public static void initiateSimulation() {
    	try {
    		//ucitavanje specifikacija za simulaciju iz config fajla
			specification = Files.readAllLines(Paths.get("config.txt"));
		} catch (IOException e1) {
			logger.log(Level.SEVERE, null, e1);
			System.exit(-1);
		}
    	try {
    		//provjeravamo da li su dimenzije matrice odgovarajuce datim zahtjevima
    	matrixDimensions = Integer.parseInt(specification.get(0));
    	if(matrixDimensions<7 || matrixDimensions>10)
    		throw new InvalidDimensionException();
    	}catch(InvalidDimensionException e)
    	{
    		//upis u loger
    		logger.log(Level.SEVERE, null, e);
			System.exit(-1);
    	}
    	try {
    	//Provjeravamo da li dati broj igraca odgovara zahtjevima zadatka
    	numberOfPlayers = Integer.parseInt(specification.get(1));
    	if(numberOfPlayers<2 || numberOfPlayers>4)
    		throw new InvalidNumberOfPlayersException();
    	}catch(InvalidNumberOfPlayersException e)
    	{
    		logger.log(Level.SEVERE, null, e);
			System.exit(-1);
    	}
    	List<String> playerNames = new ArrayList<String>();
    	var nameArray = specification.get(2).split(",");
    	try {
    		//provjeravamo da li broj navedenih imena igraca odgovara navedenom broju
    		if(nameArray.length!=numberOfPlayers)
    		{
    			throw new InvalidNumberOfPlayersException("Nije naveden pravilan broj imena");
    		}
    	}catch(InvalidNumberOfPlayersException e) {logger.log(Level.SEVERE, null, e);;
		System.exit(-1);}
    	try {
    	for(int i=0;i<nameArray.length;i++)
    	{
    		//provjeravamo jedinstvenost imena igraca
    		if(playerNames.contains(nameArray[i]))
    			throw new NotUniqueNameException();
    		else
    			playerNames.add(nameArray[i]);
    	}
    	}catch(NotUniqueNameException e) {logger.log(Level.SEVERE, null, e);
		System.exit(-1);}
    	//uzimamo takodje i broj rupa koje korisnik takodje specifikuje u config fajlu
    	numberOfHolesToGenerate = Integer.parseInt(specification.get(3));
        // Ova metoda je tu da se inicijalizuje pocetno stanje simulacije
        // Prvi korak jeste sama inicijalizacija matrice i njenih polja
        fieldMatrix = new boardCell[matrixDimensions][matrixDimensions];
        for (int i = 0; i < matrixDimensions; i++) {
            for (int j = 0; j < matrixDimensions; j++) {
                fieldMatrix[i][j] = new boardCell(i, j, matrixDimensions);
            }
        }
        // Kreiram niz igraca
        players = new Player[numberOfPlayers];
        // Kreiram sada redoslijed igraca kojim ce oni igrati
        orderOfPlayers = new ArrayList<>(numberOfPlayers);
        Random rand = new Random();
        int counterZaRedoslijed = 1;
        while (counterZaRedoslijed <= numberOfPlayers) {
            int sledeci = rand.nextInt(numberOfPlayers);
            if (!orderOfPlayers.contains(sledeci)) {
                orderOfPlayers.add(sledeci);
                counterZaRedoslijed++;
            }
        }

        int counter = 1;
        ArrayList<Integer> zaBoje = new ArrayList(numberOfPlayers);
        try {
        	//na osnovu dimenzija matrice znam iz kojeg fajla cu ucitati putanju po kojoj ce se figurice kretati
            BufferedReader fr = new BufferedReader(new FileReader(matrixDimensions + "-putanja.txt"));
            String[] putanjaString = fr.readLine().split("-");
            for (String polje : putanjaString)
                pathOfTheGame.add(Integer.parseInt(polje));
            for (Integer polje : pathOfTheGame) {
                System.out.print(polje + " ");
            }
        } catch (Exception e) {
        	logger.log(Level.SEVERE, null, e);
            System.exit(-1);
        }
        while (counter <= numberOfPlayers) {
            Player tmp = new Player();
            // postavljamo imena koja smo ucitali iz fajla
            tmp.setName(playerNames.get(counter-1));
            // Figure izgenerisati
            // Prije toga da izgenerisemo boju
            // 1-RED,2-GREEN,3-BLUE,4-YELLOW
            int boja;
            do {
                boja = rand.nextInt(4) + 1;
            } while (zaBoje.contains(boja));
            zaBoje.add(boja);
            FigureColour bojaFigure;
            if (boja == 1) {
                bojaFigure = FigureColour.RED;
            } else if (boja == 2) {
                bojaFigure = FigureColour.GREEN;
            } else if (boja == 3) {
                bojaFigure = FigureColour.BLUE;
            } else {
                bojaFigure = FigureColour.YELLOW;
            }
            tmp.setColourOfFigures(bojaFigure);
            // Generisanje figura za datog igraca
            for (int i = 0; i < 4; i++) {
                int randomNumber = rand.nextInt(3) + 1;
                GameFigure tmpFigure;
                //generisanje imena figurice na osnovu imena igraca i rednog broja figurice
                String imeFigure = tmp.getName() + "-Figura-" + (i + 1);
                if (randomNumber == 1) {
                    // Neka mi broj jedan bude obicna
                    tmpFigure = new RegularFigure(bojaFigure, imeFigure);
                } else if (randomNumber == 2) {
                    // broj dva super brza
                    tmpFigure = new ExtraFastFigure(bojaFigure, imeFigure);
                } else {
                    // broj tri lebdeca
                    tmpFigure = new FloatingFigure(bojaFigure, imeFigure);
                }
                tmp.addFigure(tmpFigure, i);
                //dodajem figuricu u skup svih figurica
                allOfTheFigures.add(tmpFigure);
                
            }
            players[orderOfPlayers.get(counter - 1)] = tmp;
            counter++;
        }
        //kreiram osnovi gui na osnovu unijetih podataka
        referenceOnGUI = new MainFrame();
    }
    //na osnovu cjelobrojnog broja vraca koordinate matrice koje odgovaraju tom broju
    public static String getCoordinatesBasedOnNumebr(int n) {
        for (int i = 0; i < matrixDimensions; i++) {
            for (int j = 0; j < matrixDimensions; j++) {
                if (i * matrixDimensions + j == n)
                    return (i + "-" + j);
            }
        }
        return null;
    }
    //generise listu nasumicnih brojeva velicine n i ciji su brojevi u datoj granici
    private static ArrayList<Integer> generateNRandomNumbers(int n,int granica)
    {
    	ArrayList<Integer> lista = new ArrayList<>();
    	Random rand = new Random();
    	for(int i =0; i<n;)
    	{
    		int generatedNumber = rand.nextInt(granica);
    		if(!lista.contains(generatedNumber))
    		{
    			lista.add(generatedNumber);
    			i++;
    		}
    	}
    	return lista;
    }
    //upis u fajl o podacima simulacije, metoda se poziva nakon zavrsetka simulacije
    public static void writeToFile()
    {
    	String fileName = "PrethodneIgre/IGRA_"+(new Date().getTime())+".txt";
    	try {
			FileWriter writer = new FileWriter(new File(fileName));
			int counter = 1;
			for(Player player : players)
			{
				writer.write("Igrac "+counter+" - " + player.getName()+"\n");
				 
				for(GameFigure  figura : player.getPlayerFigures())
				{
					var putFigurice = figura.getFigurePathClone();
					writer.write(figura.getName()+"("+figura.getType()+","+figura.getColour()+") - predjeni put (");
					for(int i=0;i<putFigurice.size();i++) {
						if(i==0)
						writer.write(putFigurice.get(i)+"-");
						else if(i==putFigurice.size()-1) {
							writer.write(putFigurice.get(i)+") - stigla do cilja ");
							if(putFigurice.size()==pathOfTheGame.size())
								writer.write("da\n\n");
								else
									writer.write("ne\n\n");
						}
						else writer.write(putFigurice.get(i)+"-");
					}
					
				}
				counter++;
			}
			writer.write("DUZINA TRAJANJA SIMULACIJE:"+((new Date().getTime()-startTime)/1000));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, null, e);
			e.printStackTrace();
		}
    }
}
