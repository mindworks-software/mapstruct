/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.collection.adder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.collection.adder._target.AdderUsageObserver;
import org.mapstruct.ap.test.collection.adder._target.IndoorPet;
import org.mapstruct.ap.test.collection.adder._target.OutdoorPet;
import org.mapstruct.ap.test.collection.adder._target.Pet;
import org.mapstruct.ap.test.collection.adder._target.Target;
import org.mapstruct.ap.test.collection.adder._target.Target2;
import org.mapstruct.ap.test.collection.adder._target.TargetDali;
import org.mapstruct.ap.test.collection.adder._target.TargetHuman;
import org.mapstruct.ap.test.collection.adder._target.TargetOnlyGetter;
import org.mapstruct.ap.test.collection.adder._target.TargetViaTargetType;
import org.mapstruct.ap.test.collection.adder._target.TargetWithAnimals;
import org.mapstruct.ap.test.collection.adder._target.TargetWithoutSetter;
import org.mapstruct.ap.test.collection.adder.source.Foo;
import org.mapstruct.ap.test.collection.adder.source.SingleElementSource;
import org.mapstruct.ap.test.collection.adder.source.Source;
import org.mapstruct.ap.test.collection.adder.source.Source2;
import org.mapstruct.ap.test.collection.adder.source.SourceTeeth;
import org.mapstruct.ap.test.collection.adder.source.SourceWithPets;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

/**
 * @author Sjaak Derksen
 */
@WithClasses({
    Source.class,
    SourceTeeth.class,
    SourceWithPets.class,
    Target.class,
    TargetDali.class,
    TargetHuman.class,
    TargetOnlyGetter.class,
    TargetViaTargetType.class,
    TargetWithoutSetter.class,
    TargetWithAnimals.class,
    SourceTargetMapper.class,
    SourceTargetMapperStrategyDefault.class,
    SourceTargetMapperStrategySetterPreferred.class,
    SourceTargetMapperWithDifferentProperties.class,
    SingleElementSource.class,
    PetMapper.class,
    TeethMapper.class,
    AdderUsageObserver.class,
    Pet.class,
    IndoorPet.class,
    OutdoorPet.class,
    DogException.class,
    CatException.class,
    Target2.class,
    Source2.class,
    Source2Target2Mapper.class,
    Foo.class
})
@RunWith(AnnotationProcessorTestRunner.class)
public class AdderTest {

    @Rule
    public final GeneratedSource generatedSource = new GeneratedSource().addComparisonToFixtureFor(
        SourceTargetMapper.class,
        SourceTargetMapperStrategyDefault.class,
        SourceTargetMapperStrategySetterPreferred.class
    );

    @IssueKey("241")
    @Test
    public void testAdd() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "mouse" ) );

        Target target = SourceTargetMapper.INSTANCE.toTarget( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( 2L );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @Test(expected = DogException.class)
    public void testAddWithExceptionInThrowsClause() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "dog" ) );

        SourceTargetMapper.INSTANCE.toTarget( source );
    }

    @Test(expected = RuntimeException.class)
    public void testAddWithExceptionNotInThrowsClause() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "cat" ) );

        SourceTargetMapper.INSTANCE.toTarget( source );
    }

    @IssueKey("241")
    @Test
    public void testAddwithExistingTarget() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "mouse" ) );

        Target target = new Target();
        target.setPets( new ArrayList<Long>( Arrays.asList( 1L ) ) );

        SourceTargetMapper.INSTANCE.toExistingTarget( source, target );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 2 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( 1L );
        assertThat( target.getPets().get( 1 ) ).isEqualTo( 2L );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @Test
    public void testShouldUseDefaultStrategy() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "mouse" ) );

        Target target = SourceTargetMapperStrategyDefault.INSTANCE.shouldFallBackToAdder( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( 2L );
        assertFalse( AdderUsageObserver.isUsed() );
    }

    @Test
    public void testShouldPreferSetterStrategyButThereIsNone() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "mouse" ) );

        TargetWithoutSetter target = SourceTargetMapperStrategySetterPreferred.INSTANCE.toTargetDontUseAdder( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( 2L );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @Test
    public void testShouldPreferHumanSingular() {

        AdderUsageObserver.setUsed( false );

        SourceTeeth source = new SourceTeeth();
        source.setTeeth( Arrays.asList( "moler" ) );

        TargetHuman target = SourceTargetMapper.INSTANCE.toTargetHuman( source );
        assertThat( target ).isNotNull();
        assertThat( target.getTeeth().size() ).isEqualTo( 1 );
        assertThat( target.getTeeth().get( 0 ) ).isEqualTo( 3 );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @Test
    public void testshouldFallBackToDaliSingularInAbsenseOfHumanSingular() {
        AdderUsageObserver.setUsed( false );

        SourceTeeth source = new SourceTeeth();
        source.setTeeth( Arrays.asList( "moler" ) );

        TargetDali target = SourceTargetMapper.INSTANCE.toTargetDali( source );
        assertThat( target ).isNotNull();
        assertThat( target.getTeeth().size() ).isEqualTo( 1 );
        assertThat( target.getTeeth().get( 0 ) ).isEqualTo( 3 );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @Test
    public void testAddReverse() throws DogException {
        AdderUsageObserver.setUsed( false );

        Target source = new Target();
        source.setPets( Arrays.asList( 3L ) );

        Source target = SourceTargetMapper.INSTANCE.toSource( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( "cat" );
    }

    @Test
    public void testAddOnlyGetter() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "mouse" ) );

        TargetOnlyGetter target = SourceTargetMapper.INSTANCE.toTargetOnlyGetter( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( 2L );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @Test
    public void testAddViaTargetType() throws DogException {
        AdderUsageObserver.setUsed( false );

        Source source = new Source();
        source.setPets( Arrays.asList( "mouse" ) );

        TargetViaTargetType target = SourceTargetMapper.INSTANCE.toTargetViaTargetType( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isNotNull();
        assertThat( target.getPets().get( 0 ).getValue() ).isEqualTo( 2L );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @IssueKey("242")
    @Test
    public void testSingleElementSource() throws DogException {
        AdderUsageObserver.setUsed( false );

        SingleElementSource source = new SingleElementSource();
        source.setPet( "mouse" );

        Target target = SourceTargetMapper.INSTANCE.fromSingleElementSource( source );
        assertThat( target ).isNotNull();
        assertThat( target.getPets().size() ).isEqualTo( 1 );
        assertThat( target.getPets().get( 0 ) ).isEqualTo( 2L );
        assertTrue( AdderUsageObserver.isUsed() );
    }

    @IssueKey( "310" )
    @Test
    public void testMissingImport() throws DogException {
        generatedSource.addComparisonToFixtureFor( Source2Target2Mapper.class );

        Source2 source = new Source2();
        source.setAttributes( Arrays.asList( new Foo() ) );

        Target2 target = Source2Target2Mapper.INSTANCE.toTarget( source );
        assertThat( target ).isNotNull();
        assertThat( target.getAttributes().size() ).isEqualTo( 1 );
    }

    @IssueKey("1478")
    @Test
    public void useIterationNameFromSource() {
        generatedSource.addComparisonToFixtureFor( SourceTargetMapperWithDifferentProperties.class );

        SourceWithPets source = new SourceWithPets();
        source.setPets( Arrays.asList( "dog", "cat" ) );

        TargetWithAnimals target = SourceTargetMapperWithDifferentProperties.INSTANCE.map( source );

        assertThat( target.getAnimals() ).containsExactly( "dog", "cat" );
    }
}
