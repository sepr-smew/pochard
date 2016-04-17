varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float sinAlpha;
uniform float sinOmega;
uniform float magnitude;

void main() {
  float x = v_texCoords.x;
  float y = v_texCoords.y;

  float dx = sin(sinOmega*x + sinAlpha)*magnitude;
  float dy = sin(sinOmega*y*1.2 + sinAlpha+1.2)*magnitude;

  float x1 = x+(dx*0.4)+(dy*0.6);
  float y1 = y+(dy*0.5)+(dx*0.5);

  gl_FragColor = v_color * texture2D(u_texture, vec2(x1, y1));
}

