/*
Copyright (c) 2013, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:
    
 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
    
 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
    
 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.
    
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package edu.ucdenver.ccp.nlp.doc;

import static org.junit.Assert.assertEquals;
import static java.lang.System.out;

import org.junit.Test;
import org.junit.Ignore;


public class XsltConverterTest {

	static final String inputFile = "/14607334.xml";
	static final String xsltFile = "/PmcOpenAccess.xsl";

	@Test
	public void testPmc() {
		String contents = XsltConverter.readFile(inputFile);		
		XsltConverter converter = new XsltConverter();
		String output = converter.convert(contents, xsltFile);
		assertEquals(
"<?xml version=\"1.0\" encoding=\"UTF-8\"?> <doc><TITLE> PINK1 Protects against Oxidative Stress by Phosphorylating Mitochondrial Chaperone TRAP1 </TITLE>     *    *     <ABSTRACT> <PARAGRAPH> Mutations in the<ITALICS> PTEN induced putative kinase 1 (PINK1) </ITALICS>gene cause an autosomal recessive form of Parkinson disease (PD). So far, no substrates of PINK1 have been reported, and the mechanism by which PINK1 mutations lead to neurodegeneration is unknown. Here we report the identification of TNF receptor-associated protein 1 (TRAP1), a mitochondrial molecular chaperone also known as heat shock protein 75 (Hsp75), as a cellular substrate for PINK1 kinase. PINK1 binds and colocalizes with TRAP1 in the mitochondria and phosphorylates TRAP1 both in vitro and in vivo. We show that PINK1 protects against oxidative-stress-induced cell death by suppressing cytochrome c release from mitochondria, and this protective action of PINK1 depends on its kinase activity to phosphorylate TRAP1. Moreover, we find that the ability of PINK1 to promote TRAP1 phosphorylation and cell survival is impaired by PD-linked PINK1 G309D, L347P, and W437X mutations. Our findings suggest a novel pathway by which PINK1 phosphorylates downstream effector TRAP1 to prevent oxidative-stress-induced apoptosis and implicate the dysregulation of this mitochondrial pathway in PD pathogenesis. </PARAGRAPH>\n" 
+ " </ABSTRACT><ABSTRACT> <PARAGRAPH> Parkinson disease (PD) is characterized by the selective loss of midbrain dopaminergic neurons. Although the cause of PD is unknown, pathological analyses have suggested the involvement of oxidative stress and mitochondrial dysfunction. Recently, an inherited form of early-onset PD has been linked to mutations in both copies of the gene encoding the mitochondrial protein PINK1. Furthermore, increasing evidence indicates that single-copy mutations in PINK1 are a significant risk factor in the development of later-onset PD. Here we show that PINK1 is a protein kinase that phosphorylates the mitochondrial molecular chaperone TRAP1 to promote cell survival. We find that PINK1 normally protects against oxidative-stress-induced cell death by suppressing cytochrome c release from mitochondria. The PINK1 mutations linked to PD impair the ability of PINK1 to phosphorylate TRAP1 and promote cell survival. Our findings reveal a novel anti-apoptotic signaling pathway that is disrupted by mutations in PINK1. We suggest that this pathway has a role in PD pathogenesis and may be a target for therapeutic intervention. </PARAGRAPH>\n"
+ " </ABSTRACT><ABSTRACT> <PARAGRAPH> Mutations in the gene that codes for PINK1 cause a common form of Parkinson disease. Here the authors show that PINK1 phosphorylates TRAP1, which suppresses apoptotic release of cytochrome c from mitochondria. </PARAGRAPH>\n"
+ " </ABSTRACT><SECTION> <PARAGRAPH> Parkinson disease (PD) is the second most common neurodegenerative disease, characterized by the selective loss of dopaminergic neurons in the substantia nigra [ 1 ]. The cause of PD, particularly the sporadic disease, is unclear, but it likely involves both genetic and environmental factors. Genetic studies have identified a number of genes associated with familial PD [ 2 ]. Postmortem analyses reveal a deficiency in the mitochondrial complex I function in patients with sporadic PD [ 3 ]. Furthermore, exposure to environmental toxins that inhibit the mitochondrial complex I can lead to PD-like phenotypes in animal models [ 4 ], suggesting the involvement of mitochondrial dysfunction in PD pathogenesis. </PARAGRAPH>\n"
+ "<PARAGRAPH> Mutations in the<ITALICS> PTEN induced putative kinase 1 (PINK1) </ITALICS>gene were originally discovered in three pedigrees with recessively inherited PD. Two homozygous<ITALICS> PINK1 </ITALICS>mutations were initially identified: a truncating nonsense mutation (W437X) and a G309D missense mutation [ 5 ]. Subsequently, multiple additional types of PD-linked mutations or truncations in<ITALICS> PINK1 </ITALICS>have been reported, making<ITALICS> PINK1 </ITALICS>the second most common causative gene of recessive PD [ 6 , 7 ]. Interestingly, despite autosomal recessive transmission of<ITALICS> PINK1 </ITALICS>-linked early-onset PD, a number of heterozygous mutations affecting only one<ITALICS> PINK1 </ITALICS>allele have been associated with late-onset PD [ 6 – 10 ]. The pathogenic mechanisms by which<ITALICS> PINK1 </ITALICS>mutations lead to neurodegeneration are unknown. </PARAGRAPH>\n"
+ "<PARAGRAPH> <ITALICS> PINK1 </ITALICS>encodes a 581-amino-acid protein with a predicted N-terminal mitochondrial targeting sequence and a conserved serine/threonine kinase domain [ 5 ]. PINK1 protein has been shown to localize in the mitochondria [ 5 , 11 – 13 ] and exhibit autophosphorylation activity in vitro [ 11 , 12 , 14 ]. The in vivo substrate(s) and biochemical function of PINK1 remain unknown. In cultured mammalian cells, overexpression of wild-type PINK1 protects cells against apoptotic stimuli [ 5 , 15 ], whereas small interfering RNA (siRNA)–mediated depletion of PINK1 increases the susceptibility to apoptotic cell death [ 16 ]. In<ITALICS> Drosophila, </ITALICS>loss of PINK1 leads to mitochondrial defects and degeneration of muscle and dopaminergic neurons [ 17 – 20 ]. Despite ample evidence indicating an essential role of PINK1 in cytoprotection, the mechanism by which PINK1 protects against apoptosis is not understood. </PARAGRAPH>\n"
+ "<PARAGRAPH> Here, we describe the characterization of mitochondrial serine/threonine kinase PINK1 and report the identification of TNF receptor-associated protein 1 (TRAP1), a mitochondrial molecular chaperone also known as heat shock protein 75 (Hsp75), as a PINK1 substrate. Our results suggest that PINK1 protects against oxidative-stress-induced apoptosis by phosphorylating downstream effector TRAP1, and provide novel insights into the pathogenic mechanisms of PINK1 mutations in causing PD. </PARAGRAPH>\n"
+ " </SECTION><DEFINITION> <PARAGRAPH> 4′,6-diamidino-2-phenylindole </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> K219A/D362A/D384A </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> 3-(4,5-dimethylthiazol-2-yl)-2,5-diphenyltetrazolium bromide </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> non-targeting </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> Parkinson disease </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> PTEN induced putative kinase 1 </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> standard error of the mean </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> small interfering RNA </PARAGRAPH>\n"
+ " </DEFINITION><DEFINITION> <PARAGRAPH> TNF receptor-associated protein 1 </PARAGRAPH>\n"
+ " </DEFINITION><FIGURE> Interaction of TRAP1 with Wild-Type and Mutant PINK1<PARAGRAPH> (A) Lysates of HeLa cells expressing C-terminally FLAG-tagged wild-type PINK1 (Input) were affinity purified with anti-FLAG M2 affinity gel, and the eluate fractions were analyzed by immunoblotting with anti-FLAG antibody. </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) Affinity-purified proteins from PINK1-transfected cells and vector control were resolved on SDS-PAGE and detected by Ponceau S staining. Arrows indicate three bands identified by mass spectrometry as TRAP1, PINK1f, and PINK1p. </PARAGRAPH>\n"
+ "<PARAGRAPH> (C) Specificity of the polyclonal anti-PINK1 antibody. Homogenates (50 μg of protein per lane) from rat brain, liver, untransfected PC12 cells, and transfected PC12 cells expressing wild-type (WT) or mutant PINK1 were analyzed by immunoblotting with anti-PINK1 and anti-actin antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (D) Endogenous PINK1 interacts with TRAP1. PC12 cell lysates (Input) were immunoprecipitated with anti-TRAP1 antibody, followed by immunoblotting with anti-PINK1 and anti-TRAP1 antibodies. IgG HC, IgG heavy chain. </PARAGRAPH>\n"
+ "<PARAGRAPH> (E) Lysates from PC12 cells transfected with FLAG-tagged wild-type or mutant PINK1 were immunoprecipitated with anti-FLAG antibody, followed by immunoblotting using anti-TRAP1 and anti-FLAG antibodies. </PARAGRAPH>\n"
+ " </FIGURE><FIGURE> PINK1 and TRAP1 Colocalize in the Mitochondrial Inner Membrane and the Intermembrane Space<PARAGRAPH> (A) Post-nuclear supernatants of PC12 cells were fractionated on a 5%–15% linear Optiprep gradient, and the fractions were analyzed by immunoblotting for PINK1, TRAP1, TIMM23, and CANX. </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) Mitochondria (Mito) isolated from PC12 cells were fractionated into matrix, inner mitochondrial membrane (IM), intermembrane space (IMS), and outer mitochondrial membrane (OM) fractions, and analyzed by immunoblotting for PINK1, TRAP1, and markers of mitochondrial subcompartments: HSPD1 (matrix), TIMM23 (IM), cytochrome c (IMS), and VDAC (OM). </PARAGRAPH>\n"
+ " </FIGURE><FIGURE> Phosphorylation of TRAP1 by Wild-Type and Mutant PINK1<PARAGRAPH> (A) In vitro kinase assays were performed by incubation of purified TRAP1 with [γ32-P] ATP in the absence (control) or presence of FLAG-tagged wild-type (WT) or mutant PINK1 proteins as indicated. Phosphorylated TRAP1 was visualized by autoradiography (top panel). PINK1 and TRAP1 proteins used in the kinase assays were shown by immunoblotting with anti-TRAP1 (middle panel) and anti-FLAG antibodies (bottom panel). </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) Normalized levels of in vitro TRAP1 phosphorylation by wild-type or mutant PINK1. Data represent mean ± standard error of the mean (SEM) from three independent experiments.*Significantly different from the wild-type PINK1 (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
+ "<PARAGRAPH> (C) PC12 cells expressing wild-type or mutant PINK1 or vector-transfected controls were treated with 400 μM H2O2as indicated. In vivo phosphorylation of endogenous TRAP1 was determined by immunoprecipitation with anti-TRAP1 antibody followed by immunoblotting using anti-phosphoserine (upper panel) and anti-TRAP1 (lower panel) antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (D) Normalized levels of in vivo TRAP1 phosphorylation by wild-type or mutant PINK1. Data represent mean ± SEM from three independent experiments.aSignificantly different from the corresponding H2O2-treated vector-transfected controls (<ITALICS> p </ITALICS>&lt; 0.01).bSignificantly different from the wild-type PINK1-transfected cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
+ "<PARAGRAPH> AU, arbitrary units. </PARAGRAPH>\n"
+ " </FIGURE><FIGURE> Wild-Type PINK1, but Not Kinase-Dead or PD-Linked Mutant PINK1, Protects against Oxidative-Stress-Induced Apoptosis<PARAGRAPH> (A) PC12 cells expressing wild-type (WT) or mutant PINK1 or vector-transfected controls were treated with 400 μM H2O2for 16 h. The extent of cell survival was assessed by using the MTT assay. Data represent mean ± SEM from three independent experiments.aSignificantly different from the vector-transfected controls (<ITALICS> p </ITALICS>&lt; 0.01).bSignificantly different from the wild-type PINK1-transfected cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) Immunoblot analysis of cytochrome c (Cyt. c) in the cytosol and mitochondria fractions isolated from PC12 cells expressing wild-type or mutant PINK1 after treatment with 400 μM H2O2for the indicated times. </PARAGRAPH>\n"
+ "<PARAGRAPH> (C) The level of cytochrome c released to the cytosol is normalized to the total level of cytochrome c in each cell sample. Data represent mean ± SEM from three independent experiments.aSignificantly different from the corresponding H2O2-treated vector-transfected controls (<ITALICS> p </ITALICS>&lt; 0.01).bSignificantly different from the wild-type PINK1-transfected cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
+ " </FIGURE><FIGURE> PINK1 Knockdown Reduces TRAP1 Phosphorylation and Protection against Oxidative Stress<PARAGRAPH> (A) PC12 cells were transfected with vehicle (control), NT siRNA, or PINK1-specific siRNAs (PINK1 siRNA-1 and PINK1 siRNA-2). The levels of PINK1 and actin in the cell lysates were analyzed by immunoblotting with anti-PINK1 and anti-actin antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) PC12 cells transfected with the indicated siRNA or vehicle (control) were treated with 400 μM H2O2for the indicated times. In vivo phosphorylation of endogenous TRAP1 was determined by immunoprecipitation with anti-TRAP1 antibody followed by immunoblotting using anti-phosphoserine and anti-TRAP1 antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (C) Normalized levels of in vivo TRAP1 phosphorylation in the control and siRNA-transfected cells. Data represent mean ± SEM from three independent experiments.*Significantly different from the corresponding H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). AU, arbitrary units. </PARAGRAPH>\n"
+ "<PARAGRAPH> (D) PC12 cells transfected with vehicle (control) or the indicated siRNAs were treated with 400 μM H2O2for 16 h. Cell viability was assessed by using the MTT assay. Data represent mean ± SEM from three independent experiments.*Significantly different from the H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
+ "<PARAGRAPH> (E) PC12 cells co-transfected with an expression vector encoding enhanced green fluorescent protein (pEGFP) and vehicle (control) or indicated siRNAs were treated with 400 μM H2O2for 16 h. Transfected cells were shown by the green fluorescence emitted by green fluorescent protein (GFP), and nuclear morphology was visualized by DAPI staining (blue). Arrowheads indicate transfected cells with apoptotic nuclei. Scale bar, 10 μm. </PARAGRAPH>\n"
+ "<PARAGRAPH> (F) Apoptosis is expressed as the percentage of transfected cells with apoptotic nuclear morphology. Data represent mean ± SEM from three independent experiments.*Significantly different from the H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). </PARAGRAPH>\n"
+ " </FIGURE><FIGURE> PINK1 Depletion Increases Cytochrome c Release from Mitochondria<PARAGRAPH> (A) PC12 cells transfected with the indicated siRNA or vehicle (control) were treated with 400 μM H2O2for the indicated times. The levels of cytochrome c (Cyt. c) and actin in the cytosol were determined by immunoblotting with anti-cytochrome c and anti-actin antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) The level of cytochrome c released to the cytosol is normalized to the level of actin in each cell sample. Data represent mean ± SEM from three independent experiments.*Significantly different from the corresponding H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.01). AU, arbitrary units. </PARAGRAPH>\n"
+ "<PARAGRAPH> (C) PC12 cells co-transfected with pEGFP and vehicle (control) or indicated siRNAs were treated with 400 μM H2O2for 16 h. Cell morphology was imaged by using phase-contrast microscopy (grey), transfected cells were visualized by the green fluorescence emitted by green fluorescent protein (GFP), and the cellular distribution of cytochrome c was detected by immunostaining with anti-cytochrome c antibody (red). Transfected cells with mitochondrial cytochrome c staining are indicated by arrows, and those with diffuse, cytosolic cytochrome c staining are indicated by arrowheads. Scale bar, 10 μm. </PARAGRAPH>\n"
+ "<PARAGRAPH> (D) Quantification of the percentage of transfected cells showing cytochrome c release. Data represent mean ± SEM from three independent experiments.*Significantly different from the H2O2-treated control cells (<ITALICS> p </ITALICS>&lt; 0.05). </PARAGRAPH>\n"
+ " </FIGURE><FIGURE> TRAP1 Depletion Abolishes the Effects of Wild-Type and Mutant PINK1 on Cell Vulnerability to Oxidative Stress<PARAGRAPH> (A) PC12 cells were transfected with vehicle (control), NT siRNA, or TRAP1-specific siRNAs (TRAP1 siRNA-1 and TRAP1 siRNA-2). The levels of TRAP1 and actin in the cell lysates were analyzed by immunoblotting with anti-TRAP1 and anti-actin antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (B) PC12 cells treated with TRAP1 siRNA-2 or NT siRNA were either untransfected (UT) or transfected with wild-type (WT) or mutant PINK1 as indicated. Vehicle-treated, non-transfected PC12 cells were used as the control. Cells were exposed to 400 μM H2O2for 16 h, and the extent of cell survival was assessed by using the MTT assay. Data represent mean ± SEM from three independent experiments. *,<ITALICS> p </ITALICS>&lt; 0.05; ns, not significant. </PARAGRAPH>\n"
+ "<PARAGRAPH> (C) PC12 cells co-transfected with pEGFP and vehicle (control) or indicated siRNAs and PINK1 plasmids were treated with 400 μM H2O2for 16 h, and the extent of apoptosis was determined by morphological analysis of DAPI-stained nuclei. The percentage of transfected cells with apoptotic nuclear morphology was quantified. Data represent mean ± SEM from three independent experiments.*,<ITALICS> p </ITALICS>&lt; 0.05; ns, not significant. </PARAGRAPH>\n"
+ "<PARAGRAPH> (D) The levels of cytochrome c (Cyt. c) and actin in the cytosol fractions from cells described in (B) were determined by immunoblotting with anti-cytochrome c and anti-actin antibodies. </PARAGRAPH>\n"
+ "<PARAGRAPH> (E) The level of cytochrome c released to the cytosol is normalized to the level of actin in each cell sample. Data represent mean ± SEM from three independent experiments.*,<ITALICS> p </ITALICS>&lt; 0.01; ns, not significant. AU, arbitrary units. </PARAGRAPH>\n"
+ "<PARAGRAPH> (F) PC12 cells co-transfected with pEGFP and vehicle (control) or indicated siRNAs and PINK1 plasmids were treated with 400 μM H2O2for 16 h, and the cellular distribution of cytochrome c was detected by immunostaining with anti-cytochrome c antibody. The percentage of transfected cells showing cytochrome c release was quantified. Data represent mean ± SEM from three independent experiments.*,<ITALICS> p </ITALICS>&lt; 0.05; ns, not significant. </PARAGRAPH>\n"
+ " </FIGURE><PARAGRAPH> All authors conceived and designed the experiments. JWP, JAO, and LSC performed the experiments and analyzed the data. LSC contributed reagents/materials/analysis tools. JWP, JAO, and LL wrote the paper. </PARAGRAPH>\n"
+ "<PARAGRAPH> This work was supported by National Institutes of Health grants NS050650 (LSC) and NS047199 and AG021489 (LL). </PARAGRAPH>\n"
+ "<PARAGRAPH> The authors have declared that no competing interests exist. </PARAGRAPH>\n"
+ "</doc> ",
			output);
	}

	static final String elsevierSimpleArt5InputFile = "/elsevier502.simple-article.xml";
	static final String elsevierSimpleArt5XsltFile = "/Art502.xsl";

	@Test
	public void testSimpleElsevierArt5() {
		String contents = XsltConverter.readFile(elsevierSimpleArt5InputFile);		
		ElsevierArt5DtdClasspathResolver resolver = new ElsevierArt5DtdClasspathResolver();
		XsltConverter converter = new XsltConverter(resolver);
		String output = converter.convert(contents, elsevierSimpleArt5XsltFile);
		// TODO add real asserts here
		assertEquals(	
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><DOC>			E-Poster abstract		<TITLE>Cardiac Imaging (E-poster 90-98)</TITLE>\n"
			+ "\n"
			+ "<PARAGRAPH>																											<FIGURE NAME=\"\"/>\n"
			+ "</PARAGRAPH>\n</DOC>",
			output);
	}

	static final String elsevierArt5InputFile = "/elsevier2.502.short.xml";
	static final String elsevierArt5XsltFile = "/Art502.xsl";

	@Test
	public void testElsevierArt5() {
		String contents = XsltConverter.readFile(elsevierArt5InputFile);		
		ElsevierArt5DtdClasspathResolver resolver = new ElsevierArt5DtdClasspathResolver();
		XsltConverter converter = new XsltConverter(resolver);
		String output = converter.convert(contents, elsevierArt5XsltFile);
		// TODO add real asserts here
		assertEquals(	
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><DOC><FIGURE NAME=\"\">Flow diagram of patients during the follow-up period.</FIGURE>\n"
+ "<FIGURE NAME=\"\">a–c 12-Month retention rates by type of medication (upper panel), and provider setting (middle panel) and length of treatment (lower panel) across weeks of the observation period.</FIGURE>\n"
+ "<FIGURE NAME=\"\">Rates (percentage of patients) of non-retention, death, and abstinence overall and by setting (N=2442).</FIGURE>\n"
+ "<FIGURE NAME=\"\">Concomitant drug use (percentage of patients) by provider setting.</FIGURE>\n"
+ " Table 1    Type of setting: Small (n=86)  Type of setting: Medium (n=101)  Type of setting: Large (36)  Physicians orientation  % family doctor  79.1%  82.3%  45.7%  % psychiatrist/neurologist  14.0%  17.7%  31.4%  % other  16.9%  0  22.9%    Psychosocial interventions (current)  Within setting  8.5%  48.6%  81.3%  Outside setting  74.3%  49.6%  32.6%    Doctors years (mean/S.D.) experience  8.2 (3.9)  8.5 (3.9)  8.5 (4.0)  Number of current pts (mean/S.D.)  5.1 (2.3)  26.4 (9.7)  118.2 (24.6)  % pts with urine test weekly  61.1%  57.4%  48.0%  % pts with take-home prescription  75.4%  57.5%  33.2%  Average no. of days take-home  2.1 (0.4)  1.9 (0.6)  1.7 (0.6)  Face-to-face contact/week (mean/S.D.)  3.7 (2.3)  4.9 (2.3)  5.6 (2.1)  Average duration of contact in minutes (mean/S.D.)  10.9 (7.4)  9.9 (6.8)  10.9 (7.6) <TITLE>Feasibility and outcome of substitution treatment of heroin-dependent patients in specialized substitution centers and primary care facilities in Germany: A naturalistic study in 2694 patients</TITLE>\n"
+ "<ABSTRACT>		Abstract		BackgroundIn many countries, buprenorphine and methadone are licensed for the maintenance treatment (MT) of opioid dependence. Despite many short-term studies, little is known about the long-term (12-month) effects of these treatments in different settings, i.e. primary care-based (PMC) and specialized substitution centers (SSCs).		ObjectivesTo describe over a period of 12 months: (1) mortality, retention and abstinence rates; (2) changes in concomitant drug use, somatic and mental health; and (3) to explore differences between different types of provider settings.		Methods12-Month prospective-longitudinal naturalistic study with four waves of assessment in a prevalence sample of N=2694 maintenance patients, recruited from a nationally representative sample of N=223 substitution physicians.		ResultsThe 12-month retention rate was 75%; the mortality rate 1.1%. 4.1% of patients became “abstinent” during follow-up. 7% were referred to drug-free addiction treatment. Concomitant drug use decreased and somatic health status improved. No significant improvements were observed for mental health and quality of life. When controlling for initial severity, small PMC settings revealed better retention, abstinence and concomitant drug use rates.		ConclusionThe study underlines the overall 12-month effectiveness of various forms of agonist MT. Findings reveal relatively high retention rates, low mortality rates, and improvements in most 12-month outcome domains, except for mental health and quality of life. PMC settings appear to be a good additional option to improve access to MTs.			</ABSTRACT>\n"
+ "		Keywords		Methadone		Buprenorphine		Epidemiology		Opioid dependence		Course		Outcome	<SECTION NAME=\"1 Introduction\">\n"
+ "<PARAGRAPH>Considerable changes have occurred in the treatment and care structure for opioid addicts over the past decade. In addition to a wide range of existing drug-free psychosocial abstinence programs (McLellan et al., 1993; Vollmer and Krauth, 2001), increased emphasis was placed in the last decade on establishing outpatient methadone maintenance therapy (MMT) and buprenorphine maintenance therapy (BMT) for the management of opioid-dependent individuals. Maintenance treatments (MTs) are provided by either large-scale specialized substitution centers (SSCs) or, more recently, by office-based physicians who either specialize fulltime on MMT/BMT treatment provision or who treat a few maintenance patients only, mostly within the context of their main function as primary care doctors (Gossop et al., 2003; Merrill, 2002; Wittchen et al., 2005). The primary short-term goals of treatment are retention in treatment, reduction of medical and social-behavioral risks, reduction of harm and mortality associated with injecting and other concomitant drug use, and interruption of the vicious circles of drug intake and criminal acts. By involving patients in a continuous treatment plan, it is also expected to reduce the burden of associated mental and somatic morbidities (psychosis, depression, HIV, viral hepatitis, etc.) and the substantial social sequelae as intermediate goals. The controversial long-term goal of maintenance regimen is – in some countries explicitly – to help patients to ultimately quit the use of drugs entirely (abstinence) and to prepare them for the decision to enroll in a drug-free abstinence program. However, due to the lack of long-term data, it is unclear to what degree this goal is realistic.</PARAGRAPH>\n"
+ "\n"
+ "<PARAGRAPH>Agonist maintenance therapies with methadone or more recently buprenorphine are currently the two most frequently used maintenance treatment strategies for opioid dependence in the care system. In clinical trials, both flexible-dose MMT and BMT have been shown to be consistently effective in at least reducing illicit opioid use (Johnson et al., 1992; Ling et al., 1976; Strain et al., 1994; Connock et al., 2007). In combination with various degrees of psychosocial support and psychological treatment other beneficial outcomes have also been demonstrated in clinical studies (review by Connock et al., 2007; Wittchen et al., 2005; Gerra et al., 2004; Gossop et al., 2001; Kakko et al., 2003; King et al., 2002; Layson-Wolf et al., 2002; Ling and Wesson, 2003; Mattick et al., 2003, 2004; Poser and Poser, 1996; Soyka et al., 1997; Waal and Haga, 2003), for example, with regard to (a) improvement of the social situation, (b) reduction of drug-related crime, (c) reduction of morbidity and mortality rates, and (d) reduction of the transmission rate of HIV.</PARAGRAPH>\n"
+ "\n"
+ "<PARAGRAPH>Despite a considerable body of research (i.e. Connock et al., 2007; Gossop et al., 2001, 2003; King et al., 2002), several significant research deficits impede further progress. First, a lack of longitudinal studies in samples of unselected substitution settings and unselected samples of patients that inform us about how MT works under routine care conditions (Law and Nutt, 2003; Connock et al., 2007) and about the degree to which the assumed intermediate and long-term goals are reached in everyday practice. This deficit is regarded as an obstacle for the wider implementation of maintenance programs (Law and Nutt, 2003). Second, there is some evidence that the beneficial effects of MMT and BMT might be robust across different types of treatment settings. However the effects of different provider models and formats remain understudied, especially with regard to subgroups of addicts. Most available studies were undertaken in a few countries (US, UK, Australia) that have considerably different regulations and traditions than other countries (Connock et al., 2007). The German treatment system, for example, has been relatively slow in adopting MMT and BMT as first-line treatments for opioid-dependent individuals. Due to the existence of a wide range of specialized in- and outpatient long-term treatment programs established in the 1970s and 1980s, MMT programs were quite rare until the 1990s and were mainly provided by relatively few SSCs with quite restrictive regulations of access (Vollmer and Krauth, 2001; Wittchen et al., 2005). Although access to treatment programs remain up to now heavily regulated and rigidly controlled with quite restrictive criteria and quality assurance measures for access and management of patients, the number of institutions licensed to administer MMT and BMT have been largely increased during the past decade. Currently (2005) in Germany there are about 2500 medical doctors (with more than 65,000 maintenance patients), trained and licensed for MT, constituting a twofold increase over the rates before the year 2000 (Wittchen et al., 2005). This increase is mainly due to increasing number of smaller office-based physicians providing maintenance treatment. Stimulated by the availability of buprenorphine (Farell et al., 2000) and by evidence that maintenance in primary care settings may work (Gossop et al., 2001, 2003; Merrill, 2002; Salsitz et al., 2000), MMT and BMT in particular is increasingly offered by primary care doctors (PMC settings) or less frequently by psychiatrists and other specialists that frequently do not generally specialize on MMT and BMT, but rather manage only a few such patients within the context of their predominant and main function as a family doctor or specialist. Such PMC settings might differ in a number of aspects (e.g. climate, resources, expertise) that can be expected to affect quality of care and outcome. For example they typically do not have additional resources in terms of time, personnel and expertise to directly offer particularly the mandatory psychological and social interventions. Irrespective of this, these doctors have to follow, though, the same stringently enforced complex legislative regulations as specialized MT centers for example by collaboration with other institutions. The relative risks and benefits of maintenance treatments in such PMC contexts remain clearly understudied (Wittchen et al., 2005; Merrill, 2002).</PARAGRAPH>\n"
+ "</SECTION>\n"
+ "<SECTION NAME=\"3.2 Retention rates and time to drop out\">\n"
+ "<PARAGRAPH>Fig. 2a–c show the retention rates from baseline to 1-year follow-up, excluding n=274 patients that terminated the maintenance program because of either abstinence or referral to abstinence programs. Adjusted for differences between groups with regard to prior treatment duration, both medication groups reveal a steady decline of retention over the 52 weeks observation time of about 25%. Fig. 2c reveals that retention during the follow-up period is associated with length of previous retention in the treatment before baseline. Patients who had just started their current maintenance therapy in the 4 weeks prior to baseline, irrespective of type of medication, had the lowest retention rate as compared to patients with &gt;6 month (82%) prior treatment (61%, HR: 2.7; 95% CI: 2.1–4.4), followed by patients with 2–6 months prior treatment (70%, HR: 2.0 95% CI: 1.6–2.4). Fig. 2b reveals better long-term retention in PMC settings as compared to SSC (HR: 1.4, 95% CI: 1.1–1.7) largely due to changes after week 42. Reasons for not retaining patients in therapy were similar in most subgroups examined, except for the methadone group, for which more patients were not retained because of imprisonment (HR: 1.7; 95% CI: 1.3–2.5).<ITALICS>n</ITALICS></PARAGRAPH>\n"
+ "</SECTION>\n"
+ "	Acknowledgements		This study is part of the collaborative COBRA study (Cost-Benefit and Risk Appraisal of Substitution Treatments, www.cobra-projekt.de), supported by the German Federal Ministry of Research and Technology (BMBF No. 01EB0440-0441/01EB0142) as part of the Addiction Research Network “ASAT” (www.asat-verbund.de). The field work and health economic program components were further supported by an unrestricted educational grant from essex pharma GmbH, Germany. We wish to thank all doctors and staff members of the participating clinics for their continued support and collaboration. Contributors. Hans-Ulrich Wittchen has planned the investigation and written the manuscript. Sabine M. Apelt provided substantial contribution to the writing of the manuscript. Dr. Gölz has participated in the recruitment of study participants and has provided counselling for the conceptualisation of the study questionnaires. Dr. Tretter provided substantial contribution to the writing of the manuscript, the interpretation, and the discussion of the results. Jens Siegert has planned and conducted the statistical analysis. Jürgen Rehm has provided substantial comments on the writing of the manuscript as well as consulting for the statistical analysis and its presentation. Gerhard Bühringer provided substantial contribution to the writing of the manuscript. Prof. Scherbaum was responsible for carrying out the study in one of the participating centers. He provided substantial contribution to the writing of the manuscript. Dr. Schäfer and Profs. Kraus, Gastpar, and Soyka provided supervision of and substantial contribution to the writing of the manuscript.	</DOC>",
			output);
	}
	
}
