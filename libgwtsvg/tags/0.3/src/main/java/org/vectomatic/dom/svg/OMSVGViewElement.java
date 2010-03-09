/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of libgwtsvg.
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
package org.vectomatic.dom.svg;

import org.vectomatic.dom.svg.impl.SVGViewElement;
import org.vectomatic.dom.svg.itf.ISVGExternalResourcesRequired;
import org.vectomatic.dom.svg.itf.ISVGFitToViewBox;
import org.vectomatic.dom.svg.itf.ISVGZoomAndPan;

public class OMSVGViewElement extends OMSVGElement implements ISVGExternalResourcesRequired, ISVGFitToViewBox, ISVGZoomAndPan {
  protected OMSVGViewElement(SVGViewElement ot) {
    super(ot);
  }

  // Implementation of the svg::SVGViewElement W3C IDL interface
  public final OMSVGStringList getViewTarget() {
    return ((SVGViewElement)ot).getViewTarget();
  }

  // Implementation of the svg::SVGFitToViewBox W3C IDL interface
  public final OMSVGAnimatedRect getViewBox() {
    return ((SVGViewElement)ot).getViewBox();
  }
  public final OMSVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
    return ((SVGViewElement)ot).getPreserveAspectRatio();
  }

  // Implementation of the svg::SVGZoomAndPan W3C IDL interface
  public final short getZoomAndPan() {
    return ((SVGViewElement)ot).getZoomAndPan();
  }
  public final void setZoomAndPan(short value) {
    ((SVGViewElement)ot).setZoomAndPan(value);
  }

  // Implementation of the svg::SVGExternalResourcesRequired W3C IDL interface
  public final OMSVGAnimatedBoolean getExternalResourcesRequired() {
    return ((SVGViewElement)ot).getExternalResourcesRequired();
  }

}
