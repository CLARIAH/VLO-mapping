/*
 * Copyright (C) 2014 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.clarin.cmdi.vlo.config;

import eu.clarin.cmdi.vlo.service.impl.ExclusiveFieldFilter;
import eu.clarin.cmdi.vlo.service.impl.InclusiveFieldFilter;
import com.google.common.collect.Sets;
import eu.clarin.cmdi.vlo.FacetConstants;
import eu.clarin.cmdi.vlo.VloWicketApplication;
import eu.clarin.cmdi.vlo.pojo.QueryFacetsSelection;
import eu.clarin.cmdi.vlo.service.FacetFieldsService;
import eu.clarin.cmdi.vlo.service.FieldFilter;
import eu.clarin.cmdi.vlo.service.PageParametersConverter;
import eu.clarin.cmdi.vlo.service.ResourceStringConverter;
import eu.clarin.cmdi.vlo.service.ResourceTypeCountingService;
import eu.clarin.cmdi.vlo.service.SearchResultsDao;
import eu.clarin.cmdi.vlo.service.SolrDocumentService;
import eu.clarin.cmdi.vlo.service.SolrFacetQueryFactory;
import eu.clarin.cmdi.vlo.service.impl.QueryFacetsSelectionParametersConverter;
import eu.clarin.cmdi.vlo.service.impl.ResourceStringConverterImpl;
import eu.clarin.cmdi.vlo.service.impl.ResourceTypeCountingServiceImpl;
import eu.clarin.cmdi.vlo.service.impl.SearchResultsDaoImpl;
import eu.clarin.cmdi.vlo.service.impl.SolrDocumentQueryFactoryImpl;
import eu.clarin.cmdi.vlo.service.impl.SolrDocumentServiceImpl;
import eu.clarin.cmdi.vlo.service.impl.SolrFacetFieldsService;
import eu.clarin.cmdi.vlo.service.impl.SolrFacetQueryFactoryImpl;
import java.io.IOException;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Annotation based Spring configuration for the VLO web application.
 *
 * Note: this works because
 * {@link org.apache.wicket.spring.SpringWebApplicationFactory} is used in place
 * of the standard Wicket application factory and annotation driven
 * configuration is enabled in WEB-INF/applicationContext.xml
 *
 * @author twagoo
 */
@Configuration
public class VloSpringConfig {

    /**
     *
     * @return the web application object that represents the Wicket application
     */
    @Bean
    public VloWicketApplication webApplication() {
        return new VloWicketApplication();
    }

    @Bean
    public VloConfig vloConfig() {
        try {
            return vloConfigFactory().newConfig();
        } catch (IOException ex) {
            throw new RuntimeException("Could not read VLO configuration", ex);
        }
    }

    @Bean
    public VloConfigFactory vloConfigFactory() {
        return new ServletVloConfigFactory();
    }

    @Bean
    public FacetFieldsService facetFieldsService() {
        return new SolrFacetFieldsService(searchResultsDao(), facetQueryFactory());
    }

    @Bean
    public SolrFacetQueryFactory facetQueryFactory() {
        return new SolrFacetQueryFactoryImpl(vloConfig().getAllFacetFields());
    }

    @Bean
    public SolrDocumentService documentService() {
        return new SolrDocumentServiceImpl(searchResultsDao(), documentQueryFactory());
    }

    @Bean
    public SearchResultsDao searchResultsDao() {
        return new SearchResultsDaoImpl(solrServer(), vloConfig());
    }

    @Bean
    public SolrDocumentQueryFactoryImpl documentQueryFactory() {
        return new SolrDocumentQueryFactoryImpl();
    }

    @Bean
    public ResourceTypeCountingService resourceTypeCountingService() {
        return new ResourceTypeCountingServiceImpl(resourceStringConverter());
    }

    @Bean
    public ResourceStringConverter resourceStringConverter() {
        return new ResourceStringConverterImpl();
    }

    @Bean
    public PageParametersConverter<QueryFacetsSelection> queryParametersConverter() {
        return new QueryFacetsSelectionParametersConverter();
    }

    @Bean
    public SolrServer solrServer() {
        return new HttpSolrServer(vloConfig().getSolrUrl());
    }

    @Bean(name = "basicPropertiesFilter")
    public FieldFilter basicPropertiesFieldFilter() {
        return new ExclusiveFieldFilter(Sets.union(IGNORE_FIELDS, TECHNICAL_FIELDS));
    }

    @Bean(name = "technicalPropertiesFilter")
    public FieldFilter technicalPropertiesFieldFilter() {
        return new InclusiveFieldFilter(TECHNICAL_FIELDS);
    }

    /**
     * Fields to be ignored. TODO: read this from config
     */
    public static final Set<String> IGNORE_FIELDS
            = Sets.newHashSet(
                    FacetConstants.FIELD_FORMAT);

    /**
     * Fields to be included in technical details. TODO: read this from config
     */
    public static final Set<String> TECHNICAL_FIELDS
            = Sets.newHashSet(
                    FacetConstants.FIELD_ID,
                    FacetConstants.FIELD_DATA_PROVIDER,
                    FacetConstants.FIELD_FORMAT,
                    FacetConstants.FIELD_LANDINGPAGE,
                    FacetConstants.FIELD_SEARCHPAGE,
                    FacetConstants.FIELD_SEARCH_SERVICE,
                    FacetConstants.FIELD_LAST_SEEN);
}
