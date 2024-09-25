@Entity
@Table(name = "scrap")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;
}