#define SNAKESONTHEPLANE


#ifndef SNAKESONTHEPLANE
void main() {
gl_FragColor.rgb = vec3(1.0);
gl_FragColor.a = 1.0;
}
#endif // SNAKES


#ifdef SNAKESONTHEPLANE

#ifdef GL_ES
	#define LOWP lowp
	#define MED mediump
	#define HIGH highp
	precision mediump float;
#else
	#define MED
	#define LOWP
	#define HIGH
#endif

#if defined(normalTextureFlag)
	#define phongFlag
#endif

#if defined(specularTextureFlag) || defined(specularColorFlag)
	#define specularFlag
#endif

#ifdef normalFlag
	varying vec3 v_normal;

	#if defined(binormalFlag) || defined(tangentFlag) || defined(normalTextureFlag)
		varying vec3 v_binormal;
		varying vec3 v_tangent;
	#endif //binormalFlag || tangentFlag
#endif //normalFlag

#if defined(colorFlag)
	varying vec4 v_color;
#endif

#ifdef blendedFlag
	varying float v_opacity;
	#ifdef alphaTestFlag
		varying float v_alphaTest;
	#endif //alphaTestFlag
#endif //blendedFlag

#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(normalTextureFlag)
	#define textureFlag
#endif

#ifdef diffuseTextureFlag
	varying MED vec2 v_diffuseUV;
#endif

#ifdef specularTextureFlag
	varying MED vec2 v_specularUV;
#endif

#ifdef normalTextureFlag
	varying MED vec2 v_normalUV;
#endif

#ifdef diffuseColorFlag
	uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
	uniform sampler2D u_diffuseTexture;
#endif

#ifdef specularColorFlag
	uniform vec4 u_specularColor;
#endif

#ifdef specularTextureFlag
	uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
	uniform sampler2D u_normalTexture;
#endif

#ifdef lightingFlag

	varying vec3 v_lightDiffuse;

	#if defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)
		#define ambientFlag
	#endif //ambientFlag

	#ifdef phongFlag
		varying vec3 v_viewVec;
		varying vec3 v_pos;

		#ifdef shininessFlag
			uniform float u_shininess;
		#else
			const float u_shininess = 20.0;
		#endif // shininessFlag

		#if defined(numDirectionalLights) && (numDirectionalLights > 0)
			struct DirectionalLight
			{
			    vec3 color;
			    vec3 direction;
			};
			uniform DirectionalLight u_dirLights[numDirectionalLights];
		#endif // numDirectionalLights

		#if defined(numPointLights) && (numPointLights > 0)
			struct PointLight
			{
			    vec3 color;
			    vec3 position;
			    float intensity;
			};
			uniform PointLight u_pointLights[numPointLights];
		#endif // numPointLights

	#else // !phongFlag

		#ifdef specularFlag
			varying vec3 v_lightSpecular;
		#endif //specularFlag

	#endif //phongFlag

	#ifdef shadowMapFlag
		uniform sampler2D u_shadowTexture;
		uniform float u_shadowPCFOffset;
		varying vec3 v_shadowMapUv;
		#define separateAmbientFlag

		float getShadowness(vec2 offset)
		{
		    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
		    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
		}

		float getShadow()
		{
		    return (//getShadowness(vec2(0,0)) +
		            getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
		            getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
		            getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
		            getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
		}
	#endif //shadowMapFlag

	#if defined(ambientFlag) && defined(separateAmbientFlag)
		varying vec3 v_ambientLight;
	#endif //separateAmbientFlag

#endif //lightingFlag

#ifdef fogFlag
	uniform vec4 u_fogColor;
	varying float v_fog;
#endif // fogFlag

void main() {
	#if defined(normalFlag) && defined(normalTextureFlag)
        vec3 normal = normalize(2.0 * texture2D(u_normalTexture, v_normalUV).xyz - 1.0);
		//vec3 normal = normalize(texture2D(u_normalTexture, v_normalUV).xyz);
        normal = normalize((v_tangent * normal.x) + (v_binormal * normal.y) + (v_normal * normal.z));
    #elif defined(normalFlag)
        vec3 normal = v_normal;
    #elif defined(normalTextureFlag)
        vec3 normal = normalize(texture2D(u_normalTexture, v_normalUV).xyz);
    #endif // normalFlag

    #if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;
    #elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    #elif defined(diffuseTextureFlag) && defined(colorFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * v_color;
    #elif defined(diffuseTextureFlag)
        vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
    #elif defined(diffuseColorFlag) && defined(colorFlag)
        vec4 diffuse = u_diffuseColor * v_color;
    #elif defined(diffuseColorFlag)
        vec4 diffuse = u_diffuseColor;
    #elif defined(colorFlag)
        vec4 diffuse = v_color;
    #else
        vec4 diffuse = vec4(1.0);
    #endif

    #if (!defined(lightingFlag))
        gl_FragColor.rgb = diffuse.rgb;
	#elif defined(phongFlag)
        vec3 lightDiffuse = v_lightDiffuse;

        #ifdef specularFlag
            vec3 lightSpecular = vec3(0.0);
            #if defined(specularTextureFlag) && defined(specularColorFlag)
                vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * u_specularColor.rgb;
            #elif defined(specularTextureFlag)
                vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb;
            #elif defined(specularColorFlag)
                vec3 specular = u_specularColor.rgb;
            #else //if defined(lightingFlag)
                vec3 specular = vec3(1.0);
            #endif
        #endif

        #if defined(numDirectionalLights) && (numDirectionalLights > 0) && (defined(normalFlag) || defined(normalTextureFlag))
            for (int i = 0; i < numDirectionalLights; i++) {
                vec3 lightDir = -u_dirLights[i].direction;
                float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
                lightDiffuse.rgb += u_dirLights[i].color * NdotL;
                #ifdef specularFlag
                    float halfDotView = dot(normal, normalize(lightDir + v_viewVec));
                    lightSpecular += u_dirLights[i].color * clamp(NdotL * pow(halfDotView, u_shininess), 0.0, 1.0);
                #endif // specularFlag
            }
        #endif // numDirectionalLights

        #if defined(numPointLights) && (numPointLights > 0) && (defined(normalFlag) || defined(normalTextureFlag))
            for (int i = 0; i < numPointLights; i++) {
                vec3 lightDir = u_pointLights[i].position - v_pos;
                float dist2 = dot(lightDir, lightDir);
                lightDir *= inversesqrt(dist2);
                float NdotL = clamp(dot(normal, lightDir), 0.0, 2.0);
                float falloff = clamp(u_pointLights[i].intensity / (1.0 + dist2), 0.0, 2.0); // FIXME mul intensity on cpu
                lightDiffuse += u_pointLights[i].color * (NdotL * falloff);
                #ifdef specularFlag
                    float halfDotView = clamp(dot(normal, normalize(lightDir + v_viewVec)), 0.0, 2.0);
                    lightSpecular += u_pointLights[i].color * clamp(NdotL * pow(halfDotView, u_shininess) * falloff, 0.0, 2.0);
                #endif // specularFlag
            }
        #endif // numPointLights

		#ifdef specularFlag
            gl_FragColor.rgb = (diffuse.rgb * lightDiffuse) + (specular * lightSpecular);
		#else
            gl_FragColor.rgb = (diffuse.rgb * lightDiffuse);
		#endif
	#elif (!defined(specularFlag))
    	#if defined(ambientFlag) && defined(separateAmbientFlag)
            #ifdef shadowMapFlag
                gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + getShadow() * v_lightDiffuse));
                //gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
            #else
                gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + v_lightDiffuse));
            #endif //shadowMapFlag
        #else
            #ifdef shadowMapFlag
                gl_FragColor.rgb = getShadow() * (diffuse.rgb * v_lightDiffuse);
            #else
                gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse);
            #endif //shadowMapFlag
		#endif
	#else //!phongFlag
        #if defined(specularTextureFlag) && defined(specularColorFlag)
            vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * u_specularColor.rgb * v_lightSpecular;
        #elif defined(specularTextureFlag)
            vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * v_lightSpecular;
        #elif defined(specularColorFlag)
            vec3 specular = u_specularColor.rgb * v_lightSpecular;
        #else
            vec3 specular = v_lightSpecular;
        #endif

        #if defined(ambientFlag) && defined(separateAmbientFlag)
            #ifdef shadowMapFlag
            gl_FragColor.rgb = (diffuse.rgb * (getShadow() * v_lightDiffuse + v_ambientLight)) + specular;
                //gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);
            #else
                gl_FragColor.rgb = (diffuse.rgb * (v_lightDiffuse + v_ambientLight)) + specular;
            #endif //shadowMapFlag
        #else
            #ifdef shadowMapFlag
                gl_FragColor.rgb = getShadow() * ((diffuse.rgb * v_lightDiffuse) + specular);
            #else
                gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular;
            #endif //shadowMapFlag
        #endif
    #endif //lightingFlag

    #ifdef fogFlag
        gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
    #endif // end fogFlag

    #ifdef blendedFlag
        gl_FragColor.a = diffuse.a * v_opacity;
        #ifdef alphaTestFlag
            if (gl_FragColor.a <= v_alphaTest)
                discard;
        #endif
    #else
        gl_FragColor.a = 1.0;
    #endif

}

#endif //SNAKES
