package com.partnet.restapi;

import com.google.gson.Gson;
import com.partnet.es.ElasticSearchClient;
import com.partnet.faers.ReactionsSearchResult;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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
		  @QueryParam("patient.patientsex") String patientSex, @QueryParam("patient.patientonsetage") String patientage, 
		  @QueryParam("patient.patientweight") String patientWeight)
  {
    Integer patientsex = patientSex != null ? Integer.valueOf(patientSex) : null;

    ReactionsSearchResult reactSearchResult = searchClient.getReactions(medicinalProduct, patientsex);
    return Response.ok(new Gson().toJson(reactSearchResult)).build();
  }

}
