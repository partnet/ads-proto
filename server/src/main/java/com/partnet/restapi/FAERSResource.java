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
import com.partnet.faers.ReactionsSearchResult;
import com.partnet.faers.ReactionsSearchResult.MetaData;
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
		return Response.ok(new Gson().toJson("Hello")).build();
	}
  @GET
  @Path("/drugs/{medicinalProduct}/reactions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getReactionOccuranceByDrug(@PathParam("medicinalProduct") String medicinalProduct, 
		  @QueryParam("patient.patientsex") String patientSex, @QueryParam("patient.patientweight") String patientWeight)
  {
    MetaData meta = new MetaData(new Date());
    Drug drug = new Drug(medicinalProduct);
    ReactionCount reactCnt1 = new ReactionCount("DIZZYNESS", 5);
    ReactionCount reactCnt2 = new ReactionCount("SCREAMING", 3);
    List<ReactionCount> reactionCnts = new ArrayList<ReactionCount>();
    reactionCnts.add(reactCnt1);
    reactionCnts.add(reactCnt2);
    ReactionsSearchResult reactSearchResult = new ReactionsSearchResult(meta, reactionCnts, drug);
    return Response.ok(new Gson().toJson(reactSearchResult)).build();
  }

}
