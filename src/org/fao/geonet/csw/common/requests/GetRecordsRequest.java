//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.csw.common.requests;

import java.util.ArrayList;
import java.util.HashSet;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.Csw.ConstraintLanguage;
import org.fao.geonet.csw.common.Csw.ElementSetName;
import org.fao.geonet.csw.common.Csw.OutputSchema;
import org.fao.geonet.csw.common.Csw.ResultType;
import org.fao.geonet.csw.common.Csw.TypeName;
import org.fao.geonet.csw.common.util.Xml;
import org.jdom.Element;

//=============================================================================

/** Params:
  *  - resultType                (0..1) Can be 'hits', 'results', 'validate'. Default is 'hits'
  *  - outputFormat              (0..1) Can be only 'application/xml'
  *  - namespace                 (0..1) Used for the GET request
  *  - outputSchema              (0..1) Can be 'ogccore', 'profile'. Default is 'ogccore'
  *  - startPosition             (0..1) Default is 1
  *  - maxRecords                (0..1) Default is 10
  *  - TypeNames                 (1..1) A set of 'dataset', 'datasetcollection', 'service', 'application'
  *  - elementSetName            (0..1) Can be 'brief', 'summary', 'full'. Default is 'summary'
  *  - constraintLanguage        (1..1) Can be 'CQL_TEXT', 'FILTER'. Must be included
  *                                     when 'constraint' is specified
  *  - constraintLanguageVersion (1..1) Example '1.0.0'
  *  - constraint                (0..1) Query to execute
  *  - distributedSearch         (0..1) TRUE|FALSE
  *  - hopCount                  (0..1) default is 2
  */

public class GetRecordsRequest extends CatalogRequest
{
	private String  outputFormat;
	private String  startPosition;
	private String  maxRecords;
	private String  constrLangVersion;
	private String  constraint;
	private String  hopCount;
	private boolean distribSearch;

	private ResultType         resultType;
	private OutputSchema       outputSchema;
	private ElementSetName     elemSetName;
	private ConstraintLanguage constrLang;

	private HashSet<TypeName> hsTypeNames = new HashSet<TypeName>();
	private ArrayList<String> alSortBy    = new ArrayList<String>();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GetRecordsRequest() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void setResultType(ResultType type)
	{
		resultType = type;
	}

	//---------------------------------------------------------------------------

	public void setOutputFormat(String format)
	{
		outputFormat = format;
	}

	//---------------------------------------------------------------------------

	public void setOutputSchema(OutputSchema schema)
	{
		outputSchema = schema;
	}

	//---------------------------------------------------------------------------

	public void setStartPosition(String start)
	{
		startPosition = start;
	}

	//---------------------------------------------------------------------------

	public void setMaxRecords(String num)
	{
		maxRecords = num;
	}

	//---------------------------------------------------------------------------

	public void setElementSetName(ElementSetName name)
	{
		elemSetName = name;
	}

	//---------------------------------------------------------------------------

	public void addTypeName(TypeName typeName)
	{
		hsTypeNames.add(typeName);
	}

	//---------------------------------------------------------------------------

	public void setConstraintLanguage(ConstraintLanguage lang)
	{
		constrLang = lang;
	}

	//---------------------------------------------------------------------------

	public void setConstraintLangVersion(String version)
	{
		constrLangVersion = version;
	}

	//---------------------------------------------------------------------------

	public void setConstraint(String constr)
	{
		constraint = constr;
	}

	//---------------------------------------------------------------------------

	public void setDistributedSearch(boolean yesno)
	{
		distribSearch = yesno;
	}

	//---------------------------------------------------------------------------

	public void setHopCount(String count)
	{
		hopCount = count;
	}

	//---------------------------------------------------------------------------

	public void addSortBy(String field, boolean ascend)
	{
		if (ascend) alSortBy.add(field +":A");
			else 		alSortBy.add(field +":D");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected String getRequestName() { return "GetRecords"; }

	//---------------------------------------------------------------------------

	protected void setupGetParams()
	{
		addParam("request", getRequestName());
		addParam("service", Csw.SERVICE);
		addParam("version", Csw.CSW_VERSION);

		addParam("resultType",     resultType);
		addParam("namespace",      Csw.NAMESPACE_CSW.getPrefix() +":"+ Csw.NAMESPACE_CSW.getURI());
		addParam("outputFormat",   outputFormat);
		addParam("outputSchema",   outputSchema, Csw.NAMESPACE_CSW.getPrefix() + ":");
		addParam("startPosition",  startPosition);
		addParam("maxRecords",     maxRecords);
		addParam("elementSetName", elemSetName);
		addParam("constraint",     constraint);
		addParam("hopCount",       hopCount);

		if (distribSearch)
			addParam("distributedSearch", "TRUE");

		addParam("constraintLanguage",          constrLang);
		addParam("constraint_language_version", constrLangVersion);

		fill("typeNames", hsTypeNames);
		fill("sortBy",    alSortBy);
	}

	//---------------------------------------------------------------------------

	protected Element getPostParams()
	{
		Element params  = new Element(getRequestName(), Csw.NAMESPACE_CSW);

		//--- 'service' and 'version' are common mandatory attributes
		setAttrib(params, "service", Csw.SERVICE);
		setAttrib(params, "version", Csw.CSW_VERSION);

		setAttrib(params, "resultType",    resultType);
		setAttrib(params, "outputFormat",  outputFormat);
		setAttrib(params, "outputSchema",  outputSchema, Csw.NAMESPACE_CSW.getPrefix() + ":");
		setAttrib(params, "startPosition", startPosition);
		setAttrib(params, "maxRecords",    maxRecords);

		if (distribSearch)
		{
			Element ds = new Element("DistributedSearch", Csw.NAMESPACE_CSW);
			ds.setText("TRUE");

			if (hopCount != null)
				ds.setAttribute("hopCount", hopCount);

			params.addContent(ds);
		}

		params.addContent(getQuery());

		return params;
	}

	//---------------------------------------------------------------------------

	private Element getQuery()
	{
		Element query = new Element("Query", Csw.NAMESPACE_CSW);

		setAttrib(query, "typeNames",      hsTypeNames, "");
		addParam (query, "ElementSetName", elemSetName);

		//--- handle constraint

		if (constraint != null && constrLang != null)
		{
			Element constr = new Element("Constraint", Csw.NAMESPACE_CSW);
			query.addContent(constr);

			if (constrLang == ConstraintLanguage.CQL)
				addParam(constr, "CqlText", constraint);
			else
				addFilter(constr);

			setAttrib(constr, "version", constrLangVersion);
		}

		//--- handle sortby

		if (alSortBy.size() != 0)
		{
			Element sortBy = new Element("SortBy", Csw.NAMESPACE_OGC);
			query.addContent(sortBy);

			for(String sortInfo : alSortBy)
			{
				String  field = sortInfo.substring(0, sortInfo.length() -2);
				boolean ascen = sortInfo.endsWith(":A");

				Element sortProp = new Element("SortProperty", Csw.NAMESPACE_OGC);
				sortBy.addContent(sortProp);

				Element propName  = new Element("PropertyName", Csw.NAMESPACE_OGC).setText(field);
				Element sortOrder = new Element("SortOrder",    Csw.NAMESPACE_OGC).setText(ascen ? "ASC" : "DESC");

				sortProp.addContent(propName);
				sortProp.addContent(sortOrder);
			}
		}

		return query;
	}

	//---------------------------------------------------------------------------

	private void addFilter(Element constr)
	{
		try
		{
			constr.addContent(Xml.loadString(constraint, false));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

//=============================================================================

