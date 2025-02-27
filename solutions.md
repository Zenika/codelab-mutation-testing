# Corrections

La base de code avec les solutions se trouvent sur la branche feat/correction_mt

## 1.4.1 Analysons la partie exception

### 1.4.1.1 TicketControllerTest

Le problème est que l'on ne teste pas la chaine de retour. En effet, le MT a réalisé des échanges entre les variables.
Un fix serait de tester la chaine complete mais aussi de s'assurer que les paramètres présents en INPUT de la méthode 
soit présent en SORTIE et au bon endroit.

```java
    @Test
    @SneakyThrows
    void bad_data_quality_not_expected() {
        var url = "/api/ticket/10";
        when(ticketServicePort.getTicketFromId(anyLong())).thenThrow(new DataQualityException(new TicketVO(1L, LocalDate.now(), List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(-12.00))));
        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message", Matchers.startsWith("Echec controle du model Facture id 1, montant -12,00 ")));
    }
```
### 1.4.1.2 ControllerExceptionHandlerTest

Le problème est que l'on test pas l'objet en sortie, on fait face à une MT de d'objet de plus il manque le test sur l'erreur 400.

```java
    @Test
    public void test_handler_exception_error_40x() {
        ResponseEntity<ApiError> ret = handler.dataNotFound(new DataNotFound("CODE"));
        assertThat(ret.getStatusCode().value()).isEqualTo(404);
        var apiError =  ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(404);
        assertThat(apiError.message()).contains("Pas de donnée en base pour le model ");
    }

    @Test
    public void test_handler_exception_error_50x() {
        ResponseEntity<ApiError> ret = handler.dataQualityException(new DataQualityException(new TicketVO(1L, LocalDate.now(), null, null, TicketStatus.UNKNOW, BigDecimal.ONE)));
        assertThat(ret.getStatusCode().value()).isEqualTo(500);
        var apiError =  ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(500);
        assertThat(apiError.message()).contains("Echec controle du model ");
    }

    @Test
    public void test_handler_exception_error_400() {
        ResponseEntity<Object> ret = handler.handleTypeMismatch(new TypeMismatchException("truc", String.class), null, null, null);
        assertThat(ret.getStatusCode().value()).isEqualTo(400);
        var apiError =  (ApiError)ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(400);
        assertThat(apiError.message()).contains("Failed to convert value of type ");
    }
```


## 1.4.2 Services

### 1.4.2.1 TicketService

Plusieurs problèmes, les tests initialement présents utilisent un jeu de donnée non représentatif (toujours des factures et des avoirs).
De plus, on a positionné un contrôle sur les montants.

Ils manquent donc des cas car les MT objets et décisions ont survécu.

```java

    @Test
    @DisplayName("Doit retourner un ticket avoir valide")
    void test_qualite_donnee_avoir() throws DataQualityException {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.AVOIR, BigDecimal.valueOf(-12.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected = ticketService.getTicketFromId(1L);
        assertThat(expected.ticketStatus().getCode()).isEqualTo("A");
        assertThat(expected).isNotNull();
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_facture_0() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, BigDecimal.valueOf(0.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Doit retourner ok car montant ==  null")
    @SneakyThrows
    void test_qualite_controle_facture_montant_null() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", null)),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, null);

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal()).isNull();
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Controle si le montant est egale à 0")
    @SneakyThrows
    void test_qualite_controle_avoir_positif() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", BigDecimal.valueOf(12.00))),
                new CaisseVO("Caisse A"), TicketStatus.AVOIR, BigDecimal.valueOf(15.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);

        assertThatThrownBy(() -> {
            ticketService.getTicketFromId(1L);
        }).isInstanceOf(DataQualityException.class);
    }


    @Test
    @DisplayName("Test get All ticket from caisse and date")
    @SneakyThrows
    void test_get_all_ticket_from_start_date_and_caisse() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", null)),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, null);

        when(repository.findAllTicketFromStartDateAndCaisse(any(), anyString())).thenReturn(List.of(ticket));
        var expected  = ticketService.getAllTicketFromStartDateAndCaisse(LocalDate.now(), "Caisse A");
        assertThat(expected).isNotNull();
        var ticketExpected = expected.get(0);
        assertThat(ticketExpected).isNotNull();
        assertThat(ticketExpected.id()).isEqualTo(1L);

    }


    @Test
    @DisplayName("Test get All ticket from date")
    @SneakyThrows
    void test_get_all_ticket_from_start_date() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "saucisson", null)),
                new CaisseVO("Caisse A"), TicketStatus.FACTURE, null);

        when(repository.findTicketAfterDate(any())).thenReturn(List.of(ticket));
        var expected  = ticketService.getAllTicketFromStartDate(LocalDate.now());
        assertThat(expected).isNotNull();
        var ticketExpected = expected.get(0);
        assertThat(ticketExpected).isNotNull();
        assertThat(ticketExpected.id()).isEqualTo(1L);

    }



    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_garantie_0() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "perceuse", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.GARANTIE, BigDecimal.valueOf(0.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);

        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_garantie_plus() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "perceuse", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.GARANTIE, BigDecimal.valueOf(10.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.valueOf(10.00).doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }


    @Test
    @DisplayName("Doit retourner ok car montant == à 0")
    @SneakyThrows
    void test_qualite_controle_garantie_minus() {
        var ticket = new TicketVO(1L, LocalDate.now(),
                List.of(new ArticleVO(1L, BigDecimal.valueOf(12), BigDecimal.valueOf(1), "perceuse", BigDecimal.valueOf(0.00))),
                new CaisseVO("Caisse A"), TicketStatus.GARANTIE, BigDecimal.valueOf(-10.00));

        when(repository.getFromId(anyLong())).thenReturn(ticket);
        var expected  = ticketService.getTicketFromId(1L);
        assertThat(expected).isNotNull();
        assertThat(expected.montantTotal().doubleValue()).isEqualTo(BigDecimal.valueOf(-10.00).doubleValue());
        assertThat(expected.articles()).isNotNull().hasSize(1);
    }
```

### 1.4.2.2 CalculCAPortService

```java
    List<TicketVO> ticketGarantie = List.of(new TicketVO(1L, LocalDate.now(), articleVOS, new CaisseVO("Caisse C"), TicketStatus.GARANTIE, BigDecimal.valueOf(20)));

    @Test
    void computeFromDate() {
        when(ticketService.getAllTicketFromStartDate(any())).thenReturn(ticketVOS);
        var caGlobal = calculCAPortService.computeFromDate(LocalDate.now());
        assertThat(caGlobal).isNotNull();
        assertThat(caGlobal.caisses()).isNotNull().hasSize(2);
        //
        assertThat(caGlobal.caTotal().doubleValue()).isEqualTo(BigDecimal.valueOf(30.00).doubleValue());
        assertThat(caGlobal.caisses().get(0).caisseVO().libelle()).hasToString("Caisse A");
        assertThat(caGlobal.caisses().get(0).ca().doubleValue()).isEqualTo(BigDecimal.valueOf(10.00).doubleValue());
        assertThat(caGlobal.caisses().get(1).caisseVO().libelle()).hasToString("Caisse B");
        assertThat(caGlobal.caisses().get(1).ca().doubleValue()).isEqualTo(BigDecimal.valueOf(20.00).doubleValue());
    }

    @Test
    void computeFromDateForCaisse() {
        when(ticketService.getAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(ticketVOSCaisseA);
        var caParCaisse = calculCAPortService.computeFromDateForCaisse(LocalDate.now(), "Caisse A");
        assertThat(caParCaisse).isNotNull();
        assertThat(caParCaisse.ca().doubleValue()).isEqualTo(10);
        assertThat(caParCaisse.caisseVO()).isNotNull();
        assertThat(caParCaisse.caisseVO().libelle()).isEqualTo("Caisse A");
    }

    @Test
    void computeFromDateForCaisse_return_default_value() {
        when(ticketService.getAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(List.of());
        var caParCaisse = calculCAPortService.computeFromDateForCaisse(LocalDate.now(), "Caisse A");
        assertThat(caParCaisse).isNotNull();
        assertThat(caParCaisse.ca().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(caParCaisse.caisseVO()).isNotNull();
        assertThat(caParCaisse.caisseVO().libelle()).isEqualTo("Caisse A");
    }


    @Test
    void computeFromDateForCaisse_return_default_value_ticket_garantie() {
        when(ticketService.getAllTicketFromStartDateAndCaisse(any(), any())).thenReturn(ticketGarantie);
        var caParCaisse = calculCAPortService.computeFromDateForCaisse(LocalDate.now(), "Caisse A");
        assertThat(caParCaisse).isNotNull();
        assertThat(caParCaisse.ca().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(caParCaisse.caisseVO()).isNotNull();
        assertThat(caParCaisse.caisseVO().libelle()).isEqualTo("Caisse A");
    }
```

## 1.5 Handler du controller

```java
    @Test
    public void test_handler_exception_error_40x() {
        ResponseEntity<ApiError> ret = handler.dataNotFound(new DataNotFound("CODE"));
        assertThat(ret.getStatusCode().value()).isEqualTo(404);
        var apiError =  ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(404);
        assertThat(apiError.message()).contains("Pas de donnée en base pour le model ");
    }

    @Test
    public void test_handler_exception_error_50x() {
        ResponseEntity<ApiError> ret = handler.dataQualityException(new DataQualityException(new TicketVO(1L, LocalDate.now(), null, null, TicketStatus.UNKNOW, BigDecimal.ONE)));
        assertThat(ret.getStatusCode().value()).isEqualTo(500);
        var apiError =  ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(500);
        assertThat(apiError.message()).contains("Echec controle du model ");
    }

    @Test
    public void test_handler_exception_error_400() {
        ResponseEntity<Object> ret = handler.handleTypeMismatch(new TypeMismatchException("truc", String.class), null, null, null);
        assertThat(ret.getStatusCode().value()).isEqualTo(400);
        var apiError =  (ApiError)ret.getBody();
        assertThat(apiError).isInstanceOf(ApiError.class);
        assertThat(apiError.codeHttp()).isEqualTo(400);
        assertThat(apiError.message()).contains("Failed to convert value of type ");
    }
```

## 1.6 Partie adaptor

## 1.6.1 TicketRepositoryPortAdaptor

Il manque le test de retour des objets, une MT de type object.
De plus on ne teste pas le cas d'une erreur custom.

```java
    @Test
    void test_convertion_ticket_vente() {
        var ticket = buildTicketEntityFacture(1L, "F", "libelle", "Caisse A");
        when(repository.findById(anyLong())).thenReturn(Optional.of(ticket));
        when(adaptorStatus.convertFromEntity(ticket.getTicketStatus())).thenReturn(TicketStatus.FACTURE);

        var ticketVO = ticketRepositoryPortAdaptor.getFromId(1L);

        // assert
        assertThat(ticketVO).isNotNull();
        assertThat(ticketVO.montantTotal()).isEqualTo(BigDecimal.valueOf(12.00));
        assertThat(ticketVO.ticketStatus().getLibelle()).isEqualToIgnoringCase(TicketStatus.FACTURE.getLibelle());
        assertThat(ticketVO.caisse().libelle()).isEqualTo("Caisse A");
        assertThat(ticketVO.dateEmission().format(DateTimeFormatter.ISO_LOCAL_DATE)).isEqualTo(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        var articles = ticketVO.articles();
        assertThat(articles).isNotNull().hasSize(2);
        var article = articles.get(0);
        assertThat(article.id()).isEqualTo(1L);
        assertThat(article).hasNoNullFieldsOrProperties();
        assertThat(article.montant()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(article.quantite()).isEqualTo(BigDecimal.valueOf(1));
        assertThat(article.prixUnite()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(article.libelle()).hasToString("pizza jambon");

    }

@Test
    void test_convertion_ticket_vente_exception() {
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> {
            ticketRepositoryPortAdaptor.getFromId(1L);
            }).isInstanceOf(DataNotFound.class).hasMessageContaining("Pas de donnée en base pour le model 1");

        }

@Test
    void test_convertion_ticket_vente_exception_input_null() {
            when(repository.findById(anyLong())).thenReturn(null);

            assertThatThrownBy(() -> {
            ticketRepositoryPortAdaptor.getFromId(1L);
            }).isInstanceOfAny(Exception.class).hasMessageContaining("Pas de donnée en base pour le model 1");

        }
```

## 1.6.1 TicketStatusPortAdaptor

```java
    @ParameterizedTest
    @ValueSource(strings = {"F", "A","R","G","T","U",})
    @DisplayName("Convertion entité en domain")
    void test_convertion_status_connu(String code) {
        var entity = buildTicketStatusEntity(code, String.format("test %s", code));

        when(repository.findByCode(anyString())).thenReturn(entity);
        var expected = ticketStatusPortAdaptor.getStatus(code);

        assertThat(expected).isNotNull();
        assertThat(expected.getCode()).isEqualTo(entity.getCode());
    }
```