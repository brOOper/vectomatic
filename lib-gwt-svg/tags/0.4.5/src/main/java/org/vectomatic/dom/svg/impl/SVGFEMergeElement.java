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
package org.vectomatic.dom.svg.impl;

import org.vectomatic.dom.svg.OMSVGAnimatedLength;
import org.vectomatic.dom.svg.OMSVGAnimatedString;

public class SVGFEMergeElement extends SVGElement {
  protected SVGFEMergeElement() {
  }

  // Implementation of the svg::SVGFEMergeElement W3C IDL interface

  // Implementation of the svg::SVGFilterPrimitiveStandardAttributes W3C IDL interface
  public final native OMSVGAnimatedLength getX() /*-{
    return this.x;
  }-*/;
  public final native OMSVGAnimatedLength getY() /*-{
    return this.y;
  }-*/;
  public final native OMSVGAnimatedLength getWidth() /*-{
    return this.width;
  }-*/;
  public final native OMSVGAnimatedLength getHeight() /*-{
    return this.height;
  }-*/;
  public final native OMSVGAnimatedString getResult() /*-{
    return this.result;
  }-*/;

}
