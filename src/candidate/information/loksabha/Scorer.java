package candidate.information.loksabha;

import java.text.DecimalFormat;

public class Scorer {
	private int age;
	private int assets;
	private int lia;
	private int eduN;
	private String party;
	private int cases;
	public static int ageLimit = 50;
	public static String partyLimit = "INC>BJP>AAP";
	public static int assetLimit = 50000000;
	public static int eduLimit = 12;
	public static int maxCases = 0;
	
	public Scorer(String party, int cases, int age, int assets, int lia, String edu) {
		this.party = party;
		this.cases = cases;
		this.age = age;
		this.assets = assets;
		this.lia = lia;
		this.eduN = Scorer.getEdu(edu);
	}
	
	public static void setParams(int ageLimit, String partyLimit, int assetsLimit, int eduLimit, int maxCases) {
		Scorer.ageLimit= ageLimit;
		Scorer.partyLimit = partyLimit;
		Scorer.assetLimit = assetsLimit;
		Scorer.maxCases  = maxCases;
		Scorer.eduLimit = eduLimit;
	}
	public static int getEdu(String edup) {
		int eduNo = 13;
		if (edup.endsWith("Pass")) {
			if (edup.startsWith("1")) eduNo = Integer.parseInt(edup.substring(0, 2));
			else eduNo = Integer.parseInt(edup.substring(0, 1));
		}
		else if (edup.contains("Graduate")) {
			if (edup.startsWith("G")) eduNo = 16;
			else if (edup.startsWith("P")) eduNo = 18;
			else eduNo = 16;
		}
		else if (edup.contains("Doctorate")) eduNo = 23;
		else if (edup.contains("Illiterate")) eduNo = 0;
		else if (edup.endsWith("nal")) eduNo = 15;
		else eduNo = 13;
		return eduNo;
	}
	public int score() {
		int score = 0;
		if(age<=ageLimit) score++;
		String[] tokens= partyLimit.split(">");
		for (String t:tokens) {
			if (party.equals(t)) {
				score++;
				break;
			}
		}
		if ((assets-lia)<= assetLimit && assets>=100000) score++;
		if (lia<=assets/10) score++;
		if (eduN>=eduLimit) score++;
		if (cases<=maxCases) score++;
		return score;
	}
	public static String convertMoneyToString(int ass) {
		String assetsS = null;
		DecimalFormat f = new DecimalFormat("##.00");
	     if (ass<100000) assetsS = "Rs " + ass + " only";
	     else if (ass<10000000) {
	    	 assetsS = "Rs " +  f.format(ass/100000.00) + " lakhs";
	     }
	     else {
	    	 assetsS = "Rs " + f.format(ass/10000000.00) + " crores";
	     }
		return assetsS;
	}
	public static String convertEduNtoString(int eduN) {
		if (eduN==0) return "Illiterate";
		else if (eduN<=12) return eduN+"th Class";
		else if (eduN<=16) return "Graduate";
		else if (eduN<=18) return "Post Graduate";
		else return "Doctorate";
		
	}
}
