/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
/*
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package org.vectomatic.dom.svg;

import java.util.List;

import com.google.gwt.core.client.JavaScriptException;

/**
 * The {@link org.vectomatic.dom.svg.OMSVGICCColor} interface expresses an
 * ICC-based color specification. <p>Note: The {@link org.vectomatic.dom.svg.OMSVGICCColor}
 * interface is deprecated, and may be dropped from future versions of the
 * SVG specification.</p>
 */
public class OMSVGICCColor {
  private String colorProfile;
  public List<Integer> colors;
  public OMSVGICCColor(String colorProfile, List<Integer> colors) {
    this.colorProfile = colorProfile;
    this.colors = colors;
  }

  @Override
  public int hashCode() {
	return colorProfile.hashCode() + colors.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
	if (obj instanceof OMSVGICCColor) {
	  OMSVGICCColor c = (OMSVGICCColor)obj;
	  return colorProfile.equals(c.colorProfile)
	  	&& colors.equals(c.colors);
	}
	return false;
  }

  // Implementation of the svg::SVGICCColor W3C IDL interface
  /**
   * The name of the color profile, which is the first parameter of an ICC color
   * specification.
   */
  public final String getColorProfile() {
    return this.colorProfile;
  }
  /**
   * The name of the color profile, which is the first parameter of an ICC color
   * specification.
   * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR) Raised on an attempt
   * to change the value of a <a href="svgdom.html#ReadOnlyNodes">read only
   * attribute</a>.
   */
  public final void setColorProfile(String value) throws JavaScriptException {
    this.colorProfile = value;
  }
  /**
   * The list of color values that define this ICC color. Each color value is
   * an arbitrary floating point number.
   */
  public final OMSVGNumberList getColors() {
	return null;
  }
  @Override
  public String toString() {
	StringBuilder builder = new StringBuilder("OMSVGICCColor(");
	builder.append(colorProfile);
	if (colors != null) {
	  builder.append(", ");
	  builder.append(colors);
	}
    builder.append(")");
	return builder.toString();
  }
}
