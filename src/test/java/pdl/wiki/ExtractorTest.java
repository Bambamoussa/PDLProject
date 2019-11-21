 package pdl.wiki;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.String;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Classe de test pour la classe Extractor
 */
public class ExtractorTest
{

    Extractor extractorwiki;
    Extractor extractorhtml;
    String UrlWithTables;
    Map<String, Integer> nbtabliens;
    ArrayList<String> liens;
    List<List<String>> csvwiki;
    List<List<String>> csvhtml;
    List<String> csvTest;
    Map<String,Map<String,Integer>> cellsTest;

    @BeforeEach
    public void setUp() throws Exception
    {
        extractorhtml = new HTMLExtractor();
        extractorwiki = new WikiTextExtractor();
        UrlWithTables = "https://fr.wikipedia.org/w/index.php?title=Championnat_d%27Allemagne_f%C3%A9minin_de_handball&oldid=160723522";
        liens = new ArrayList<>();
        liens.add("https://fr.wikipedia.org/w/index.php?title=Th%C3%BCringer_HC&oldid=161132172");
        liens.add("https://fr.wikipedia.org/w/index.php?title=Championnat_d%27Allemagne_f%C3%A9minin_de_handball&oldid=160723522");
        liens.add("https://fr.wikipedia.org/w/index.php?title=Parti_communiste_de_l%27Union_sovi%C3%A9tique&oldid=160293234");
        liens.add("https://fr.wikipedia.org/w/index.php?title=Union_des_r%C3%A9publiques_socialistes_sovi%C3%A9tiques&oldid=163482866");
        liens.add("https://fr.wikipedia.org/w/index.php?title=Oulan-Bator&oldid=163654075");
        nbtabliens = new HashMap<>();
        nbtabliens.put("https://fr.wikipedia.org/w/index.php?title=Th%C3%BCringer_HC&oldid=161132172", 1);
        nbtabliens.put("https://fr.wikipedia.org/w/index.php?title=Championnat_d%27Allemagne_f%C3%A9minin_de_handball&oldid=160723522", 6);
        nbtabliens.put("https://fr.wikipedia.org/w/index.php?title=Parti_communiste_de_l%27Union_sovi%C3%A9tique&oldid=160293234", 3);
        nbtabliens.put("https://fr.wikipedia.org/w/index.php?title=Union_des_r%C3%A9publiques_socialistes_sovi%C3%A9tiques&oldid=163482866", 2);
        nbtabliens.put("https://fr.wikipedia.org/w/index.php?title=Oulan-Bator&oldid=163654075", 1);
        csvTest = new ArrayList<>();
        for (int i = 1; i < 6; i++)
        {
            csvTest.add(FileUtils.readFileToString(new File("inputdata" + File.separator + "PDL" + i + ".csv")));
        }
        cellsTest = new HashMap<>();
        
    }

    @Test
    public void getCSVHTML()
    {
        //test du nombre de tableau trouvé
        for (String lien : liens)
        {
            int htmlSize = extractorhtml.getCSV(new Url(lien)).size();
            assertTrue( nbtabliens.get(lien) == htmlSize,"nombre de tableau trouvé incorrecte (extractor HTML, lien:" + lien + "; prévu : )" + nbtabliens.get(lien) + ", reçu : " + htmlSize);
        }
    }
    
    @Test
    public void getCSVWikiText()
    {
        //test du nombre de tableau trouvé
        for (String lien : liens)
        {
            int wikitextSize = extractorwiki.getCSV(new Url(lien)).size();
            assertTrue( nbtabliens.get(lien) == wikitextSize,"nombre de tableau trouvé incorrecte (extractor wiki, lien:" + lien + "; prévu : )" + nbtabliens.get(lien) + ", reçu : " + wikitextSize);
        }
    }

    @Test
    public void getCSV2HTML() throws IOException
    {
        csvhtml = extractorhtml.getCSV(new Url(UrlWithTables));
        for (int i = 0; i < 5; i++)
        {
        	int htmlsizeLig = csvhtml.get(i).size();
        	int csvsizeLig =countCsvLines(csvTest.get(i));
            assertTrue(htmlsizeLig ==csvsizeLig, "Nombre de lignes du CSV différent trouvé (HTML), prévu :" + htmlsizeLig + "; reçu :" + csvsizeLig);
            int csvsizeCol = countCsvCol(csvTest.get(i));
            numcol = colNumber(UrlWithTables);
            int htmlsizeCol = numcol.get(i);
            assertTrue(htmlsizeCol ==csvsizeCol, "Nombre de colonnes du CSV différent trouvé (HTML), prévu :" + htmlsizeCol + "; reçu :" + csvsizeCol);
            assertTrue(textCells(UrlWithTables,3,0,3)==getString(csvTest,3,0,3), "Contenu de la cellule du CSV difféent du contenue de la cellule du site");
            assertTrue(textCells(UrlWithTables,0,0,0)==getString(csvTest,0,0,0), "Contenu de la cellule du CSV difféent du contenue de la cellule du site");
            assertTrue(textCells(UrlWithTables,3,5,0)==getString(csvTest,3,5,0), "Contenu de la cellule du CSV difféent du contenue de la cellule du site");
        }
    }
    
    @Test
    public void getCSV2WikiText() throws IOException
    {
        csvwiki = extractorwiki.getCSV(new Url(UrlWithTables));
        for (int i = 0; i < 5; i++)
        {
        	int wikisizeLig = csvwiki.get(i).size();
        	int csvsizeLig =countCsvLines(csvTest.get(i));
            assertTrue(wikisizeLig == csvsizeLig, "Nombre de lignes du CSV différent trouvé (Wiki), prévu :" + wikisizeLig + "; reçu :" + csvsizeLig);
            int csvsizeCol = countCsvCol(csvTest.get(i));
            numcol = colNumber(UrlWithTables);
            int wikisizeCol = numcol.get(i);
            assertTrue(wikisizeCol ==csvsizeCol, "Nombre de colonnes du CSV différent trouvé (HTML), prévu :" + wikisizeCol + "; reçu :" + csvsizeCol);
            assertTrue(textCells(UrlWithTables,3,0,3)==getString(csvTest,3,0,3), "Contenu de la cellule du CSV difféent du contenue de la cellule du site");
            assertTrue(textCells(UrlWithTables,0,0,0)==getString(csvTest,0,0,0), "Contenu de la cellule du CSV difféent du contenue de la cellule du site");
            assertTrue(textCells(UrlWithTables,3,5,0)==getString(csvTest,3,5,0), "Contenu de la cellule du CSV difféent du contenue de la cellule du site");
        }
    }
 
    //retourne le nombre de lignes ou colonnes du fichier text CSV
    private int countCsvLines(String csv) throws IOException
    {
        InputStream is = new ByteArrayInputStream(csv.getBytes());
        try
        {
            byte[] c = new byte[1024];
            int nbLig = 0;
            int nbCharLu = 0;
            boolean fichierVide = true;
            while ((nbCharLu = is.read(c)) != -1)
            {
                fichierVide = false;
                for (int i = 0; i < nbCharLu; ++i)
                {
                        if (c[i] == '\n')
                        {
                            nbLig++;
                        }
                    
                }
            }
            return (nbLig == 0 && !fichierVide) ? 1 : nbLig;
        }
        finally
        {
            is.close();
        }
    }
    
    private int countCsvCol(String csv) throws IOException
    {
        InputStream is = new ByteArrayInputStream(csv.getBytes());
        try
        {
            byte[] c = new byte[1024];
            int nbCol = 1;
            int nbCharLu = 0;
            boolean fichierVide = true;
            while ((nbCharLu = is.read(c)) != -1)
            {
                fichierVide = false;
                for (int i = 0; i < nbCharLu; ++i)
                {
                    if (c[i] == ';')
                        {
                            nbCol++;
                        }
                    else if(c[i] == '\n') {
                    	return nbCol;
                    }
                }
                  
            }
            return (nbCol == 0 && !fichierVide) ? 1 : nbCol;
        }
        finally
        {
            is.close();
        }
    }
    

    ArrayList<Integer> numcol;
	private String[][] tab;

    public  ArrayList<Integer> colNumber(String url) throws IOException {
    	Document page = Jsoup.connect(url).get();
        Elements tables = page.select(".wikitable");
        numcol = new  ArrayList<Integer>();
        
        for (Element table : tables)
        {
        	int tot = 0;
        	for (Element colspan :table.select("td, th")) {
        		String valcol = colspan.attr("colspan");
        		if(valcol !="")tot += Integer.parseInt(valcol)-1;
        		String valrow = colspan.attr("rowspan");
        		if(valrow !="")tot += Integer.parseInt(valrow)-1;
        	}

            int nbcol = (table.select("td, th").size()+tot)/table.select("tr").size();
            numcol.add(nbcol);
        }
        return numcol;
    }
    
    
    public  String textCells(String url, int tableau, int lignes, int cols) throws IOException {
    	Document page = Jsoup.connect(url).get();
        Elements tables = page.select(".wikitable");
        int i = 0;
        
        for (Element table : tables)
        {
        	if(i == tableau) {
        		int j =0;
        		tab = new String[30][30]; 
	        	for (Element ligne : table.select("tr")) {
	        		int c=0;
        			for(Element cellule : ligne.select("td, th")) {
	        			if(cellule.attr("rowspan") != "") {
    	        			String rowValue = cellule.text();
        					int cellsRows = Integer.parseInt(cellule.attr("rowspan"))+j;
        					for(int lig = j; lig<cellsRows; lig++) {
        						tab[lig][c]=rowValue;
        					}
	        			}else if(cellule.attr("colspan") != "") {
	        				String colValue = cellule.text();
        					int cellsCols = Integer.parseInt(cellule.attr("colspan"))+c;
        					for (int col = c; col<cellsCols;col++) {
    	        				tab[j][col]=colValue;
        					}
	        			}else {
	        				while(tab[j][c] != null) {
	        					c++;
	        				}
        					String cellValue = cellule.text();
	        				tab[j][c]=cellValue;
	        			}
	        			c++;
	        		}
	        		j++;
	        	}
	        	return tab[lignes][cols];
        	}i++;
        }
        return "Rien";
    }
    
	public String getString(List<String> csvTest,int tb,int lignes, int cols) throws IOException{
		String csv = csvTest.get(tb-1);
    	InputStream is = new ByteArrayInputStream(csv.getBytes());
        try
        {
            byte[] c = new byte[1024];
            int nbCol = 1;
            int nbCharLu = 0;
            boolean fichierVide = true;
            while ((nbCharLu = is.read(c)) != -1)
            {
                fichierVide = false;
                String res = new String (c);
                String tablig[] =res.split("/n");
                String strglig = tablig[lignes];
                String tabcol[] = strglig.split(";");
                String strgcol = tabcol[cols];
                System.out.println(strgcol);
                return strgcol;
            }
            
            return "Rien";
        }
        finally
        {
            is.close();
        }
    }

//    @After
//    public void Check()
//    {
//
//    }
}