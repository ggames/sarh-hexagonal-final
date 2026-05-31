package com.fich.sarh.relationshipofposition.domain.model;

import com.fich.sarh.position.domain.model.Position;
import lombok.*;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter
public class RelationshipPosition {
    Long id;
    Position parent;
    Position child;
}
