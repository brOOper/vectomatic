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


public class OMSVGPathSegCurvetoCubicRel extends OMSVGPathSeg {
  protected OMSVGPathSegCurvetoCubicRel() {
  }

  // Implementation of the svg::SVGPathSegCurvetoCubicRel W3C IDL interface
  public final native float getX() /*-{
    return this.x;
  }-*/;
  public final native void setX(float value) /*-{
    this.x = value;
  }-*/;
  public final native float getY() /*-{
    return this.y;
  }-*/;
  public final native void setY(float value) /*-{
    this.y = value;
  }-*/;
  public final native float getX1() /*-{
    return this.x1;
  }-*/;
  public final native void setX1(float value) /*-{
    this.x1 = value;
  }-*/;
  public final native float getY1() /*-{
    return this.y1;
  }-*/;
  public final native void setY1(float value) /*-{
    this.y1 = value;
  }-*/;
  public final native float getX2() /*-{
    return this.x2;
  }-*/;
  public final native void setX2(float value) /*-{
    this.x2 = value;
  }-*/;
  public final native float getY2() /*-{
    return this.y2;
  }-*/;
  public final native void setY2(float value) /*-{
    this.y2 = value;
  }-*/;

}