package com.ingemark.codegeneration;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
    "com.ingemark.codegeneration.GenerateController",
    "com.ingemark.codegeneration.GenerateService",
    "com.ingemark.codegeneration.GenerateRepository"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CodeGeneratorProcessor extends AbstractProcessor
{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateController.class))
        {
            generateController((TypeElement) element);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateService.class))
        {
            generateService((TypeElement) element);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateRepository.class))
        {
            generateRepository((TypeElement) element);
        }

        return true;
    }

    private void generateController(TypeElement element)
    {
        String entityName = element.getSimpleName()
            .toString();
        String controllerName = entityName + "Controller";
        String packageName = processingEnv.getElementUtils()
            .getPackageOf(element)
            .toString();

        GenerateController annotation = element.getAnnotation(GenerateController.class);
        String resourceBaseName = (annotation != null)
            ? annotation.resourceBaseName()
            : entityName.toLowerCase();

        ClassName serviceClass = ClassName.get(packageName, entityName + "Service");
        ClassName dtoConverterClass = ClassName.get("com.ingemark.productapp.app.util.dto", "DtoConverter");
        ClassName entityClass = ClassName.get(packageName, entityName);
        ClassName dtoClass = ClassName.get(packageName, entityName + "Dto");

        MethodSpec constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(serviceClass, "service")
            .addParameter(ParameterizedTypeName.get(dtoConverterClass, entityClass, dtoClass), "dtoConverter")
            .addStatement("super(service, dtoConverter, $T.class, $T.class)", entityClass, dtoClass)
            .build();

        TypeSpec controller = TypeSpec.classBuilder(controllerName)
            .addModifiers(Modifier.PUBLIC)
            .superclass(ParameterizedTypeName.get(ClassName.get("com.ingemark.productapp.app.entity.identifiable",
                "IdentifiableEntityController"), entityClass, dtoClass))
            .addAnnotation(RestController.class)
            .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                .addMember("value", "$S", "api/v1/" + resourceBaseName)
                .addMember("produces", "$T.APPLICATION_JSON_VALUE", MediaType.class)
                .build())
            .addMethod(constructor)
            .build();

        writeToFile(packageName, controller);
    }

    private void generateService(TypeElement element)
    {
        String entityName = element.getSimpleName()
            .toString();
        String serviceName = entityName + "Service";
        String repositoryName = entityName + "Repository";
        String packageName = processingEnv.getElementUtils()
            .getPackageOf(element)
            .toString();

        MethodSpec constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(packageName, repositoryName), "repository")
            .addStatement("super(repository)")
            .build();

        TypeSpec service = TypeSpec.classBuilder(serviceName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Service.class)
            .superclass(ParameterizedTypeName.get(ClassName.get("com.ingemark.productapp.app.entity.identifiable",
                "IdentifiableEntityService"), ClassName.get(packageName, entityName)))
            .addMethod(constructor)
            .build();

        writeToFile(packageName, service);
    }

    private void generateRepository(TypeElement element)
    {
        String entityName = element.getSimpleName()
            .toString();
        String repositoryName = entityName + "Repository";
        String packageName = processingEnv.getElementUtils()
            .getPackageOf(element)
            .toString();

        TypeSpec repository = TypeSpec.interfaceBuilder(repositoryName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Repository.class)
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get("com.ingemark.productapp.app.entity.identifiable",
                "IdentifiableEntityRepository"), ClassName.get(packageName, entityName), ClassName.get(Integer.class)))
            .build();

        writeToFile(packageName, repository);
    }

    private void writeToFile(String packageName, TypeSpec typeSpec)
    {
        try
        {
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();
            javaFile.writeTo(processingEnv.getFiler());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

