package com.ingemark.productapp.app;

import java.util.function.BiFunction;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class TestHelper
{
    private static final String
        JWT
        = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJub3QiLCJyb2xlIjoiW1JPTEVfVVNFUl0iLCJpYXQiOjE3NDE3OTg5MzUsImV4cCI6MTc0MTgwMjUzNX0.TQCiStlwoNY3WzCavbdfLbvUo24iDa8vb-tsqjwal3s";

    @Autowired
    private ObjectMapper mapper;

    public MockHttpServletRequestBuilder createDeleteRequest(String uri, int id)
    {
        return buildRequest(MockMvcRequestBuilders::delete, uri + "/{id}", new Object[]{ id });
    }

    public MockHttpServletRequestBuilder createDeleteRequest(String uri)
    {
        return buildRequest(MockMvcRequestBuilders::delete, uri, new Object[]{});
    }

    public MockHttpServletRequestBuilder createGetByIdRequest(String uri, int id)
    {
        return buildRequest(MockMvcRequestBuilders::get, uri + "/{id}", new Object[]{ id });
    }

    public MockHttpServletRequestBuilder createGetRequest(String uri)
    {
        return buildRequest(MockMvcRequestBuilders::get, uri, new Object[]{});
    }

    public MockHttpServletRequestBuilder createPostByIdRequest(String uri, Integer id)
    {
        return buildRequest(MockMvcRequestBuilders::post, uri, new Object[]{ id });
    }

    public MockHttpServletRequestBuilder createPostRequest(String uri) throws Exception
    {
        return buildRequest(MockMvcRequestBuilders::post, uri, new Object[]{});
    }

    public MockHttpServletRequestBuilder createPostRequest(String uri, Object object) throws Exception
    {
        return buildRequest(MockMvcRequestBuilders::post,
            uri,
            new Object[]{}).content(mapper.writeValueAsBytes(object));
    }

    public MockHttpServletRequestBuilder createUpdateRequest(String uri, Object id, Object object) throws Exception
    {
        return buildRequest(MockMvcRequestBuilders::put,
            uri + "/{id}",
            new Object[]{ id }).content(mapper.writeValueAsBytes(object));
    }

    public MockHttpServletRequestBuilder createUpdateRequest(String uri, Object object) throws Exception
    {
        return buildRequest(MockMvcRequestBuilders::put, uri, new Object[]{}).content(mapper.writeValueAsBytes(object));
    }

    public MockHttpServletRequestBuilder buildRequest(
        BiFunction<String, Object[], MockHttpServletRequestBuilder> request,
        String urlTemplate,
        Object[] uriVars)
    {
        return request.apply(urlTemplate, uriVars)
            .header("Authorization", "Bearer " + JWT)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
