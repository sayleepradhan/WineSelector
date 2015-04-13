package dinnerplanner.wineselecter;
import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import org.apache.log4j.PropertyConfigurator;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

class WineDetails {
	String wineColor;
	String wineBody;
	String wineRegion;
	int[] wineCostRange;
	int heuristicType;
	String continent;
	Scanner sc = new Scanner(System.in);
	
	//getter methods 
	
	String getWineColor(){
		int type;
		int iFlag = 0;
		String wineColor="";
		while (iFlag==0)
		{
			System.out.println("Enter the Type of Wine:\n 1. Red Wine\n 2. White Wine\n 3. Rose Wine");
			type = sc.nextInt();
			if (type>=1 && type<=3)
			{
				iFlag=1;
				switch(type){
					case 1 : wineColor="RedWine"; break;
					case 2 : wineColor="WhiteWine"; break;
					case 3 : wineColor="RoseWine"; break;
					}
			}
			else
				System.out.println("Invalid Wine Type! Enter Wine Type again.");
		}
		return wineColor;
	}
	
	String getWineBody(){
		int body;
		int iFlag = 0;
		String wineBody="";
		while (iFlag==0)
		{
			System.out.println("Enter the Type of Wine Body:\n1. Full\n2. Medium\n3. Light");
			body = sc.nextInt();
			if (body>=1 && body<=3)
			{
				iFlag=1;
				switch(body){
					case 1 : wineBody="Full"; break;
					case 2 : wineBody="Medium"; break;
					case 3 : wineBody="Light"; break;
					}
			}
			else
				System.out.println("Invalid Wine Body! Enter Type of Wine Body again.");
		}
		return wineBody;
	}
	
	int[] getCostRange(){
		int costRange[] = {0,0};
		System.out.println("Enter the minimum Cost of Wine:");
		costRange[0] = sc.nextInt();
		System.out.println("Enter the maximum Cost of Wine:");
		costRange[1] = sc.nextInt();	
		return costRange;
	}
	String getWineRegion(){
		int region;
		int iFlag = 0;
		String wineRegion="";
		while (iFlag==0)
		{
			System.out.println("Enter the type of Region:\n1.Mediterranean \n2.Continental\n3.Maritime\n");
			region = sc.nextInt();
			if (region>=1 && region<=3)
			{
				iFlag=1;
				switch(region){
					case 1 : wineRegion="MediterraneanRegion"; break;
					case 2 : wineRegion="ContinentalRegion"; break;
					case 3 : wineRegion="MaritimeRegion"; break;
					}
			}
			else
				System.out.println("Invalid Region type! Enter the Region type again.");
		}
		return wineRegion;
	}
	String getContinent(){
		int option;
		int iFlag = 0;
		String continent="";
		while (iFlag==0)
		{
			System.out.println("Enter the name of the continent:\n1.Australia\n2.Europe\n3.North America\n"
					+"4.South America");
			option = sc.nextInt();
			if (option>=1 && option<=4)
			{
				iFlag=1;
				switch(option){
					case 1 : continent="Australia"; break;
					case 2 : continent="Europe"; break;
					case 3 : continent="NorthAmerica"; break;
					case 4 : continent="SouthAmerica"; break;
					}
			}
			else
				System.out.println("Invalid Continent name! Enter the Name of the Continent again.");
		}
		return continent;
	}
	int getHeuristicType(){
		int choice=1;
		int iFlag = 0;
		while (iFlag==0)
		{
			System.out.println("Enter the type of heuristic for the search: 1. By Cost of Wine \n2. By Size of Wine Bottle.");
			choice = sc.nextInt();
			if (choice==1 || choice ==2)
				iFlag=1;
			else
				System.out.println("Invalid Country! Enter Country of Origin again.");
		}
		
		return choice;
	}
	
	WineDetails(){
		this.wineColor=getWineColor();
		this.wineBody=getWineBody();
		this.continent = getContinent();
		this.wineRegion=getWineRegion();
		this.wineCostRange = getCostRange();
		this.heuristicType=getHeuristicType();
	}
}


class WineSelector {
	
	String fileName ="C:/Users/Saylee/Documents/Sem1/AI/FinalProject/OWLfiles/dinnerPlanner.owl";
	OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	WineDetails winedtl = new WineDetails();
	
	private static class ValueComparator<K , V extends Comparable<V>> implements Comparator<K>
	{
		Map<K, V> map;
 
		public ValueComparator(Map<K, V> map) {
			this.map = map;
		}
 
		@Override
		public int compare(K keyA, K keyB) {
			Comparable<V> valueA = map.get(keyA);
			V valueB = map.get(keyB);
			return valueA.compareTo(valueB);
		}
	}
 
	public static<K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> unsortedMap)
	{
		Map<K, V> sortedMap = new
				TreeMap<K, V>(new ValueComparator<K, V>(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}

	void searchByCost(ResultSet results){
		Map<RDFNode, Integer> minCostMap = new HashMap<RDFNode, Integer>();
		minCostMap = computeMinCost(results,this.model);
		System.out.println("Wines that satisfy the first criteria: Wine Color");
		printMap(minCostMap);
		System.out.println("\nWines that satisfy the second criteria: Wine Body");
		minCostMap = getWineBody(minCostMap);
		//System.out.println("Size of mincostMap : "+minCostMap.size());  // Level 1 : Find Wines with given Wine Body
		printMap(minCostMap);
		System.out.println("\nWines that satisfy the third and fourth criteria: Wine Region and Continent");
		minCostMap = getIndCostMap(minCostMap);
		printMap(minCostMap);								// Level 2 : Find Wines that were grown in the given Country
		minCostMap = findGoalNodes(minCostMap);				//Level 3 : Find Wines that lie within the given cost range
		System.out.println("\nWines that satisfy the last criteria: Cost Range");
		printMap(minCostMap);
	}
	Map<RDFNode, Integer> computeMinCost(ResultSet wines,OntModel model)
	{
			Map<RDFNode, Integer> minCostMap = new HashMap<RDFNode, Integer>();
		while (wines.hasNext())
		{
			QuerySolution qs = wines.nextSolution();
			RDFNode n = qs.get("subject");
			//System.out.println("Inside computeMinCost");
			//System.out.println(n.toString());
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "select ?subject ?amount\n" +
					 " WHERE { ?subject rdf:type owl:NamedIndividual ; \n"+
					 "rdf:type <"+n.asNode().toString()+">; \n"+ 
					 "dp:hasCost ?amount .} \n"+
					 "ORDER BY ?amount \n"+
					 "LIMIT 1";
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution qe = QueryExecutionFactory.create(query,model);
			ResultSet results = qe.execSelect();
			int minAmount =1000;
			minCostMap.put(n, minAmount);
			if (results.hasNext()){
				QuerySolution qs2 = results.nextSolution();
				RDFNode minCost = qs2.get("amount");
				char[] c = minCost.toString().toCharArray();
				int i=0;
				String s = "";
				while (c[i]!='^'){
					s += Character.toString(c[i]);
					i++;
				}
				//System.out.println(s);
				int amount = Integer.parseInt(s);
				if (amount<minAmount)
				{
					minAmount = amount;
					minCostMap.put(n, minAmount);
				}
			}	
		}
		return sortByValue(minCostMap);
	}
	
	Map<RDFNode, Integer> getWineBody(Map<RDFNode, Integer> costMap){ //gets the wines with the given wine body
		Map<RDFNode, Integer> rdfMap = new HashMap<RDFNode, Integer>();
			for (Entry e: costMap.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			//boolean output = this.searchWineAttr(n,this.winedtl.wineBody);
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "select ?ind ?amount\n"+
					 "WHERE {\n"+
					 "?ind dp:hasBody dp:"+this.winedtl.wineBody+";\n"+
					 " rdf:type owl:NamedIndividual;\n"+
					 "rdf:type <"+node.asNode().toString()+">;\n"+
					 "dp:hasCost ?amount.} ORDER BY ?amount";
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution qe = QueryExecutionFactory.create(query,model);
			ResultSet results = qe.execSelect();
			while (results.hasNext()){
				QuerySolution qs = results.nextSolution();
				RDFNode key = qs.get("ind");
				RDFNode value = qs.get("amount");
				//System.out.println("Key - "+key.toString());
				//System.out.println("Value - "+value.toString());
				if (value!=null){
					char[] c = value.toString().toCharArray();
					int i=0;
					String s = "";
					while (c[i]!='^'){
						s += Character.toString(c[i]);
						i++;
					}
					//System.out.println(s);
				int amount = Integer.parseInt(s);
				rdfMap.put(key, amount);
				//System.out.println(key.toString() +"--> "+amount);
			}	
		}	
	}
	//System.out.println("Size of rdfMap in getWineBody method  at the end : "+rdfMap.size());
	//printMap(rdfMap);
	return sortByValue(rdfMap);
	}
	Map<RDFNode, Integer> getIndCostMap(Map<RDFNode, Integer> costMap){
		//System.out.println("Size of map initially in getIndCostMap method: "+costMap.size());
		Map<RDFNode, Integer> costMapByGeo = new HashMap<RDFNode, Integer>();
		for(Entry e: costMap.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			//System.out.println("Inside getIndCostMap");
			//System.out.println(node.asNode().toString());
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "SELECT ?amount\n" +
					 " WHERE { \n"+
					 "<"+node.asNode().toString()+"> rdf:type ?winetype; \n"+
					 "dp:hasCost ?amount;\n"+
					 "dp:grownInVineyard ?vineyard ;\n"+
					 "rdf:type owl:NamedIndividual.\n"+
					 "?vineyard rdf:type owl:NamedIndividual.\n"+
					 "?vineyard rdf:type ?country ;\n"+
					 "dp:hasRegion dp:"+this.winedtl.wineRegion+".\n"+
					 "?country rdfs:subClassOf dp:"+this.winedtl.continent+".}\n"+
					  "ORDER BY ?amount ";
			Query query = QueryFactory.create(sparqlQuery);
			//System.out.println(node.asNode().toString());
			QueryExecution qe = QueryExecutionFactory.create(query,model);
			ResultSet results = qe.execSelect();
			 if (results.hasNext()){
				//QuerySolution qs = results.nextSolution();
				//RDFNode
				costMapByGeo.put(node, (int) e.getValue());
			}
		}
		//System.out.println("Map Size : "+ costMapByGeo.size());
		return sortByValue(costMapByGeo);
	}
	Map<RDFNode, Integer> findGoalNodes(Map<RDFNode, Integer> minCostByRegionMap)
	{
		Map<RDFNode, Integer> goalNodes = new HashMap<RDFNode, Integer>();
		for (Entry e : minCostByRegionMap.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			int cost = (int) e.getValue();
			if (cost>=this.winedtl.wineCostRange[0] && cost<=this.winedtl.wineCostRange[1])
			{
				goalNodes.put(node, cost);
				//System.out.println("Wine Name:------------"+e.getKey().toString());
				//System.out.println("Wine Cost:---------"+e.getValue().toString());
			}	
		}
		//System.out.println("inside findGoalNodes. Size of List  "+ goalNodes.size());
		return sortByValue(goalNodes);
	}
	void searchWineByBottleSize(ResultSet results){
		Map<RDFNode, Float> bottleSizeMap = new HashMap<RDFNode, Float>();
		bottleSizeMap = this.computeBottleSizeMap(results);
		System.out.println("Wines that satisfy the first criteria: Wine Color");
		printMapBySize(bottleSizeMap);		
		System.out.println("\nWines that satisfy the second criteria: Wine Body");
		bottleSizeMap = this.getWineBodyBySize(bottleSizeMap);
		printMapBySize(bottleSizeMap);
		System.out.println("\nWines that satisfy the third and fourth criteria: Wine Region and Continent");
		bottleSizeMap = getIndBotSizeMap(bottleSizeMap);
		printMapBySize(bottleSizeMap);
		Map<RDFNode,Integer> finalGoalMap = new HashMap<RDFNode,Integer>();									
		System.out.println("\nWines that satisfy the last criteria: Cost Range");
		finalGoalMap = findGoalNodesBySize(bottleSizeMap);
		printMap(finalGoalMap);
		
	}
	Map<RDFNode, Float> computeBottleSizeMap(ResultSet wines){
		Map<RDFNode, Float> bottleSizeMap = new HashMap<RDFNode, Float>();
		while (wines.hasNext())
		{
			QuerySolution qs = wines.nextSolution();
			RDFNode n = qs.get("subject");
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "select ?subject ?bottleSize\n" +
					 "WHERE { ?subject rdf:type owl:NamedIndividual ;\n"+
					 "rdf:type <"+n.asNode().toString()+">; \n"+ 
					 "dp:hasBottleSize ?bottleSize .} \n"+
					 "ORDER BY ?bottleSize LIMIT 1";
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution qe = QueryExecutionFactory.create(query,model);
			ResultSet results = qe.execSelect();
			while (results.hasNext()){
				QuerySolution qs2 = results.nextSolution();
				RDFNode maxBottleSize = qs2.get("bottleSize");
				char[] c = maxBottleSize.toString().toCharArray();
				int i=0;
				String s = "";
				while (c[i]!='^'){
					s += Character.toString(c[i]);
					i++;
				}
				Float size = Float.parseFloat(s);
				bottleSizeMap.put(n, size);
				}
			}
		return sortByValue(bottleSizeMap);
	}
	Map<RDFNode, Float> getWineBodyBySize(Map<RDFNode, Float> sizeMap){ //gets the wines with the given wine body
		//System.out.println("inside getWineBodySize. sizeMap initial size : "+sizeMap.size());
		Map<RDFNode, Float> rdfMap = new HashMap<RDFNode, Float>();
			for (Entry e: sizeMap.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "select ?ind ?size\n"+
					 "WHERE {\n"+
					 "?ind dp:hasBody dp:"+this.winedtl.wineBody+";\n"+
					 " rdf:type owl:NamedIndividual;\n"+
					 "rdf:type <"+node.asNode().toString()+">;\n"+
					 "dp:hasBottleSize ?size.} ORDER BY ?size ";
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution qe = QueryExecutionFactory.create(query,model);
			ResultSet results = qe.execSelect();
			while (results.hasNext()){
				QuerySolution qs = results.nextSolution();
				RDFNode key = qs.get("ind");
				RDFNode value = qs.get("size");
				//System.out.println("Size of bottle : " + value.toString());
				if (value!=null){
					char[] c = value.toString().toCharArray();
					int i=0;
					String s = "";
					while (c[i]!='^'){
						s += Character.toString(c[i]);
						i++;
					}
				Float size = Float.parseFloat(s);
				rdfMap.put(key, size); 
			}	
		}	
	}
	return sortByValue(rdfMap);
	}
	
	Map<RDFNode, Float> getIndBotSizeMap(Map<RDFNode, Float> map){
		Map<RDFNode, Float> sizeMapByGeo = new HashMap<RDFNode, Float>();
		for(Entry e: map.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "SELECT ?size\n" +
					 " WHERE { \n"+
					 "<"+node.asNode().toString()+"> rdf:type ?winetype; \n"+
					 "dp:hasBottleSize ?size;\n"+
					 "dp:grownInVineyard ?vineyard ;\n"+
					 "rdf:type owl:NamedIndividual.\n"+
					 "?vineyard rdf:type owl:NamedIndividual.\n"+
					 "?vineyard rdf:type ?country ;\n"+
					 "dp:hasRegion dp:"+this.winedtl.wineRegion+".\n"+
					 "?country rdfs:subClassOf dp:"+this.winedtl.continent+".}\n"+
					  "ORDER BY ?amount ";
			Query query = QueryFactory.create(sparqlQuery);
			//System.out.println(node.asNode().toString());
			QueryExecution qe = QueryExecutionFactory.create(query,this.model);
			ResultSet results = qe.execSelect();
			if (results.hasNext()){
				sizeMapByGeo.put(node, (float) e.getValue());
			}
		}
	return sortByValue(sizeMapByGeo);
	}
	Map<RDFNode, Integer> findGoalNodesBySize(Map<RDFNode, Float> bottleSizeByRegionMap)
	{
		Map<RDFNode, Integer> goalNodes = new HashMap<RDFNode, Integer>();
		for (Entry e : bottleSizeByRegionMap.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
					 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
					 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
					 "select ?amount\n"+
					 "WHERE {<"+node.asNode().toString()+"> dp:hasCost ?amount;"
					 		+ "rdf:type owl:NamedIndividual .}";
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution qe = QueryExecutionFactory.create(query,this.model);
			ResultSet result = qe.execSelect();
			
			while(result.hasNext()){
				QuerySolution qs = result.nextSolution();
				RDFNode value = qs.get("amount");
				if (value!=null){
					char[] c = value.toString().toCharArray();
					int i=0;
					String s = "";
					while (c[i]!='^'){
						s += Character.toString(c[i]);
						i++;
					}
				int amount = Integer.parseInt(s);
				if (amount>=this.winedtl.wineCostRange[0] && amount<=this.winedtl.wineCostRange[1])
				{
					goalNodes.put(node,amount);
				}	
		
			}
		}
	}
		return sortByValue(goalNodes);
}
	
	void printMap(Map<RDFNode, Integer> map){
		for (Entry e : map.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			int cost = (int) e.getValue();
				System.out.println("Wine Name : "+node.toString());
				System.out.println("Wine Cost : "+cost);
			}	
		}
	void printMapBySize(Map<RDFNode, Float> map){
		for (Entry e : map.entrySet()){
			RDFNode node = (RDFNode) e.getKey();
			float bottleSize = (float) e.getValue();
				System.out.println("Wine Name : "+node.toString());
				System.out.println("Wine Bottle Size : "+bottleSize);
			}	
		}
	public static void main(String args[]){
		String log4jConfPath = "C:/Users/Saylee/workspace/DinnerPlanner/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		WineSelector ws = new WineSelector();
		ws.model.setNsPrefix("owl", OWL.getURI());
		ws.model.setNsPrefix("rdf", RDF.getURI());
		ws.model.setNsPrefix("RDFS", RDFS.getURI());
		ws.model.setNsPrefix("dp", "http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#");
		
		try {
				File file = new File(ws.fileName);
				FileInputStream reader = new FileInputStream(file);
				ws.model.read(reader,null);
			}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
				 "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
				 "PREFIX dp: <http://www.semanticweb.org/saylee/ontologies/2014/10/dinnerPlanner.owl#>\n" +
				 " SELECT ?subject \n" +
				 " WHERE { ?subject rdfs:subClassOf dp:"+ws.winedtl.wineColor+"}\n";
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution qe = QueryExecutionFactory.create(query,ws.model);
		
		// Method to display the result on the console
		ResultSet results = qe.execSelect();
		if (ws.winedtl.heuristicType==1)
			ws.searchByCost(results);
		else
			ws.searchWineByBottleSize(results);		
	}
}