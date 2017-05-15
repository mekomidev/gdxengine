package com.mekomidev.testgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mekomidev.gdxengine.Game;
import com.mekomidev.gdxengine.ecs.Entity;
import com.mekomidev.gdxengine.ecs.EntityManager;
import com.mekomidev.gdxengine.ecs.EntitySystem;
import com.mekomidev.gdxengine.graphics.g3d.CameraComponent;
import com.mekomidev.gdxengine.graphics.g3d.ModelComponent;
import com.mekomidev.gdxengine.graphics.g3d.ModelRenderingSystem;

public class TexturedBallEntity extends Entity {
	
	ModelComponent mc;
	CameraComponent cc;

	public TexturedBallEntity(EntityManager manager, String name) {
		super(manager, name);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	public void create() {
		// Environment
		ModelRenderingSystem renderer = Game.stage.getSystem(EntitySystem.map.getSubclassId(ModelRenderingSystem.class));
		//renderer.environment.//set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        renderer.environment.add(new DirectionalLight().set(1f, 0.894f, 0.807f, -1f, -0.8f, -0.2f));
		
		// Material
        Texture diffuse = new Texture("textures/Pinewood_Bark_DIFF.png"),
        		normal = new Texture("textures/Pinewood_Bark_NORM.png"),
        		specular = new Texture("textures/Pinewood_Bark_SPEC.png");
        
        diffuse.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        normal.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        specular.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
        
        Material mat = new Material();
		mat.set(TextureAttribute.createDiffuse(diffuse),
				TextureAttribute.createNormal(normal),
				TextureAttribute.createSpecular(specular));
		
		// Model
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		builder.node().id = "demo";
		builder.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, mat).cylinder(10f, 20f, 10f, 32);//.sphere(15f, 15f, 15f, 32, 32);//
		Model model = builder.end();
		
		FileHandle f = Gdx.files.classpath("com/mekomidev/gdxengine/graphics/g3d/shaders/");
		//for(FileHandle fs : f.list())
		//	System.out.println(fs.name());
		
		// Shader
		/*
		DefaultShaderProvider shaderProvider = new DefaultShaderProvider(Gdx.files.classpath("com/mekomidev/gdxengine/graphics/g3d/shaders/default.vertex.glsl"),
																		Gdx.files.classpath("com/mekomidev/gdxengine/graphics/g3d/shaders/default.fragment.glsl"));
																		*/
		DefaultShaderProvider shaderProvider = new DefaultShaderProvider(Gdx.files.internal("shaders/default.vertex.glsl"),
																		Gdx.files.internal("shaders/default.fragment.glsl"));
		
		mc = addComponent(ModelComponent.class);
		mc.model = new ModelInstance(model);
		mc.shader = shaderProvider.getShader(new Renderable());
		
		// Camera
		cc = addComponent(CameraComponent.class);
		PerspectiveCamera cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        
        cc.cam = cam;
	}

	@Override
	public void render() {
		
	}
	

	float time = 0;

	@Override
	public void update(float delta) {
		// Rotate sphere
		mc.model.transform.rotate(0, 1, 1, 10 * delta);
	}

	@Override
	public void dispose() {
		mc.model.model.dispose();
	}

	@Override
	public void triggerEvent(String eventName, Object... blob) {
		// TODO Auto-generated method stub
		
	}

}
