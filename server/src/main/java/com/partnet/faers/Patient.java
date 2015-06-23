package com.partnet.faers;

import java.util.ArrayList;
import java.util.List;

/**
 * Data holder for patient information parsed from FAERS xml. Used to serialize to JSON
 *
 * @author jwhite
 */
public class Patient {
  public Float patientonsetage;
  public Float patientweight;
  public Integer patientsex;

  public List<Reaction> reactions = new ArrayList<>();
  public List<Drug> drugs = new ArrayList<>();
}