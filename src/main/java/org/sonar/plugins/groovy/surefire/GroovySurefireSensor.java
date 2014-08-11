/*
 * Sonar Groovy Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.groovy.surefire;

import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.plugins.groovy.foundation.Groovy;
import org.sonar.plugins.surefire.api.SurefireUtils;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DependedUpon("surefire-java")
public class GroovySurefireSensor implements Sensor {

  private static Logger LOGGER = LoggerFactory.getLogger(GroovySurefireSensor.class);

  private final Settings settings;
  private final FileSystem fs;
  private final ResourcePerspectives perspectives;
  private final Project project;

  public GroovySurefireSensor(Settings settings, FileSystem fs, ResourcePerspectives perspectives, Project project) {
    this.settings = settings;
    this.fs = fs;
    this.perspectives = perspectives;
    this.project = project;
  }

  @DependsUpon
  public Class dependsUponCoverageSensors() {
    return CoverageExtension.class;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return fs.hasFiles(fs.predicates().hasLanguage(Groovy.KEY));
  }

  public void analyse(Project project, SensorContext context) {
    File dir = SurefireUtils.getReportsDirectory(settings, project);
    collect(context, dir);
  }

  protected void collect(SensorContext context, File reportsDir) {
    LOGGER.info("parsing {}", reportsDir);
    new GroovySurefireParser(perspectives, project, fs).collect(context, reportsDir);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
