package com.partnet.restapi;

import com.google.gson.Gson;
import com.partnet.es.ElasticSearchClient;
import com.partnet.faers.DrugSearchResult;
import com.partnet.faers.ReactionsSearchResult;
import com.partnet.faers.SafetyReport;
import com.partnet.util.Range;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;



/**
 * REST Resource for accessing FAERS (FDA ADVERSE EVENT REPORTING SYSTEM) data to implement the features of the application.
 *
 * @author Steffanie VanderVeen
 */
@Path("/faers")
public class FAERSResource
{
  @Inject
  private ElasticSearchClient searchClient;

  @GET
	@Path("/drugs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDrugs(@QueryParam("term") String term) {
    //String drugs = searchClient.getDrugs();
    String[] drugs =  new String[]{"Abilify",                                
        "Advil",
        "Aleve",
        "Celebrex",
        "Claritin",
        "Colace",
        "Crestor",
        "Cymbalta",
        "Diovan",
        "Dulcolax",
        "Excedrin",
        "Gaviscon",
        "Lantus",
        "Lotrimin",
        "Lyrica",
        "Maalox Antacid",
        "Midol",
        "Nexium",
        "Synthroid",
        "Vyvanse"};
    drugs = Arrays.stream(drugs).filter(x -> x.contains(term)).toArray(String[]::new);
    return Response.ok(new Gson().toJson(drugs)).build();
  }

	@GET
	@Path("/drugs/{medicinalProduct}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDrugDetails(@PathParam("medicinalProduct") String medicinalProduct) {
    final DrugSearchResult drugSearchResult = searchClient.getDrugs(medicinalProduct);
    return Response.ok(new Gson().toJson(drugSearchResult)).build();
  }

  @GET
  @Path("/drugs/{medicinalProduct}/reactions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getReactionOccuranceByDrug(@PathParam("medicinalProduct") String medicinalProduct, 
		  @QueryParam("patient.sex") String patientSex, @QueryParam("patient.age.low") String patientAgeLow,
      @QueryParam("patient.age.high") String patientAgeHigh, @QueryParam("patient.weight.low") String patientWeightLow,
      @QueryParam("patient.weight.high") String patientWeightHigh)
  {

    try {
      Integer patientsex = patientSex != null ? Integer.valueOf(patientSex) : null;
      Range ageRange = patientAgeLow != null && patientAgeHigh != null ?
          new Range(Float.valueOf(patientAgeLow), Float.valueOf(patientAgeHigh)) : null;
      Range weightRange = patientWeightLow != null && patientAgeHigh != null ?
          new Range(Float.valueOf(patientWeightLow), Float.valueOf(patientWeightHigh)) : null;

      ReactionsSearchResult reactSearchResult = searchClient.getReactions(medicinalProduct, patientsex, ageRange, weightRange);
      return Response.ok(new Gson().toJson(reactSearchResult)).build();

    } catch (NumberFormatException e) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

  }

  @GET
  @Path("/reports")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSafetyReports(@QueryParam("medicinalproduct") String medicinalproduct,
      @QueryParam("reactionmeddrapt") String reactionmeddrapt,
      @QueryParam("patient.sex") String patientSex, @QueryParam("patient.age.low") String patientAgeLow,
      @QueryParam("patient.age.high") String patientAgeHigh, @QueryParam("patient.weight.low") String patientWeightLow,
      @QueryParam("patient.weight.high") String patientWeightHigh, @QueryParam("count") boolean count)
  {
    try {
      Integer patientsex = patientSex != null ? Integer.valueOf(patientSex) : null;
      Range ageRange = patientAgeLow != null && patientAgeHigh != null ?
          new Range(Float.valueOf(patientAgeLow), Float.valueOf(patientAgeHigh)) : null;
      Range weightRange = patientWeightLow != null && patientAgeHigh != null ?
          new Range(Float.valueOf(patientWeightLow), Float.valueOf(patientWeightHigh)) : null;

      if (count) {
        final long reportCount = searchClient.getReportCount(medicinalproduct, reactionmeddrapt, patientsex, ageRange, weightRange);
        return Response.ok("report count: " + reportCount + ", drug: " + medicinalproduct + ", reaction: " + reactionmeddrapt).build();
      }
      else {
        final List<SafetyReport> safetyReports = searchClient.getSafetyReports(medicinalproduct, reactionmeddrapt, patientsex, ageRange, weightRange);
        return Response.ok(new Gson().toJson(safetyReports)).build();
      }

    } catch (NumberFormatException e) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
  }

}
