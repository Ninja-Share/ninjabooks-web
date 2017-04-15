package com.ninjabooks.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Database DAO config layer. Use this config bean container if you need
 * inject any DAO to controller or service.
 * @see MySQLConfig
 *
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@Configuration
@ComponentScan(basePackageClasses = MySQLConfig.class)
public class DBDaoConfig
{
}
