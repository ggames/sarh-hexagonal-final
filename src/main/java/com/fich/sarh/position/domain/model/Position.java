package com.fich.sarh.position.domain.model;

import com.fich.sarh.common.StatusOfPositions;
import com.fich.sarh.organizationalunit.domain.model.OrganizationalUnit;
import com.fich.sarh.point.domain.model.Point;
import com.fich.sarh.transformation.domain.model.Transformation;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Position {

    Long id;
    Point point;
    OrganizationalUnit organizationalUnit;
    StatusOfPositions positionStatus;
    boolean active;
    // Position newPosition;
   // List<Position> originPosition;
    Long pointsAvailable;
    Transformation creationResolution;
    Transformation resolutionSuppression;

    @Builder.Default
    List<Position> parents = new ArrayList<>();

    public void addParent(Position parent){

        if(parent == null) return;

        if(this.parents == null){
            this.parents = new ArrayList<>();
        }

        boolean alreadyExists = this.parents.stream()
                .anyMatch(p -> p.getId().equals(parent.getId()));

        if(!alreadyExists) {
            this.parents.add(parent);
        }

    }

    public void removeParent(Long parentId){
        if(parents == null) {
            return;
        }
        parents.removeIf( p -> p.getId().equals(parentId));
    }
    public void clearParent() {
        if(parents != null) {
            parents.clear();
        }
    }

    public boolean hasParent(){

        return parents != null && !parents.isEmpty();
    }

    public List<Long> getParentIds(){
        if(parents == null || parents.isEmpty()){
            return  List.of();
        }
        return parents.stream()
                .map(Position::getId)
                .toList();
    }

    @Override
    public String toString() {
        return "Position{ ID= "
                + id +
                "pointID=" + point +
                ", organizationalU" +
                "nitID=" + organizationalUnit +
                ", positionStatus=" + positionStatus +

              //  ", originPosition=" + originPosition +
                ", pointsAvailable=" + pointsAvailable +
                ", creationResolutionID=" + creationResolution +
                ", resolutionSuppressionID=" + resolutionSuppression +
                ", Parents =" + parents +
                '}';
    }
}
