package com.android.local.service.processor;

import com.android.local.service.annotation.Get;
import com.android.local.service.annotation.Page;
import com.android.local.service.annotation.Service;
import com.android.local.service.processor.helper.ALSProcessorHelper;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class ALSProcessor extends AbstractProcessor {

    private ALSProcessorHelper alsProcessorHelper;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        alsProcessorHelper = new ALSProcessorHelper(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(Get.class.getCanonicalName());
        annotationTypes.add(Page.class.getCanonicalName());
        annotationTypes.add(Service.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;
        for (TypeElement annotation : annotations) {
            String annotationClassName = annotation.getQualifiedName().toString();
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (element instanceof TypeElement) {//类
                    if (Service.class.getName().equals(annotationClassName)) {
                        alsProcessorHelper.processService((TypeElement) element);
                    }
                } else if (element instanceof ExecutableElement) {//方法
                    if (Page.class.getName().equals(annotationClassName)) {
                        alsProcessorHelper.processPage((ExecutableElement) element);
                    } else if (Get.class.getName().equals(annotationClassName)) {
                        alsProcessorHelper.processGet((ExecutableElement) element);
                    }
                }
            }
        }
        alsProcessorHelper.createClassFile();
        return true;
    }
}