package com.partnet.restapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.partnet.faers.Drug;
import com.partnet.faers.DrugSearchResult;
import com.partnet.faers.DrugSearchResult.IndicationCount;
import com.partnet.faers.ReactionsSearchResult;
import com.partnet.faers.ReactionsSearchResult.MetaData;
import com.partnet.faers.ReactionsSearchResult.OutcomeCount;
import com.partnet.faers.ReactionsSearchResult.ReactionCount;



/**
 * REST Resource for accessing FAERS (FDA ADVERSE EVENT REPORTING SYSTEM) data to implement the features of the application.
 *
 * @author Steffanie VanderVeen
 */
@Path("/faers")
public class FAERSResource
{

	@GET
	@Path("/drugs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDrugs() {
		return Response.ok(new Gson().toJson("Hello")).build();
	}

	@GET
	@Path("/drugs/{medicinalProduct}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDrugDetails(@PathParam("medicinalProduct") String medicinalProduct) {
	  
	  MetaData meta = new MetaData(new Date());
    Drug drug = new Drug(medicinalProduct);
    IndicationCount indication1 = new IndicationCount("CHRONIC MYELOMONOCYTIC LEUKAEMIA", 25);
    List<IndicationCount> indications = new ArrayList<IndicationCount>();
    indications.add(indication1);
    List<Double> treatmentDurations = new ArrayList<Double>();
    treatmentDurations.add(100d);
    treatmentDurations.add(50d);
	  DrugSearchResult drugSearchResult = new DrugSearchResult(drug, indications, treatmentDurations);
		return Response.ok(new Gson().toJson(drugSearchResult)).build();
	}
  @GET
  @Path("/drugs/{medicinalProduct}/reactions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getReactionOccuranceByDrug(@PathParam("medicinalProduct") String medicinalProduct, 
		  @QueryParam("patient.patientsex") String patientSex, @QueryParam("patient.patientonsetage") String patientage, 
		  @QueryParam("patient.patientweight") String patientWeight)
  {
    MetaData meta = new MetaData(new Date());
    Drug drug = new Drug(medicinalProduct);
    OutcomeCount outcome1 = new OutcomeCount(5, 25);
    List<OutcomeCount> outcomes = new ArrayList<OutcomeCount>();
    outcomes.add(outcome1);
    ReactionCount reactCnt1 = new ReactionCount("DIZZYNESS", outcomes, 5, 10, 15, 50);
    ReactionCount reactCnt2 = new ReactionCount("SCREAMING", outcomes, 3, 12, 6, 50);
    List<ReactionCount> reactionCnts = new ArrayList<ReactionCount>();
    reactionCnts.add(reactCnt1);
    reactionCnts.add(reactCnt2);
    ReactionsSearchResult reactSearchResult = new ReactionsSearchResult(meta, reactionCnts, drug);
    return Response.ok(new Gson().toJson(reactSearchResult)).build();
  }

}
